package com.sap.iot.starterkit.cert;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.sap.iot.starterkit.cert.type.Authentication;
import com.sap.iot.starterkit.cert.type.Device;
import com.sap.iot.starterkit.cert.type.Envelope;
import com.sap.iot.starterkit.cert.type.Message;
import com.sap.iot.starterkit.cert.type.SampleMessage;

import sun.security.pkcs10.PKCS10;

@SuppressWarnings("restriction")
public class Main {

	private static Properties properties;

	private static KeyStoreClient keyStoreClient;

	private static HttpClient httpClient;

	private static Gson jsonParser;

	public static void main(String[] args)
	throws Exception {

		// initialization step

		properties = readProperties();
		jsonParser = new Gson();
		httpClient = new HttpClient();
		keyStoreClient = new KeyStoreClient();

		// decide whether to use proxy or not

		boolean useProxy = Boolean.parseBoolean(properties.get("use.proxy").toString());
		if (useProxy) {

			System.out.println("Go via a proxy");
			System.out.println("----------------------------------------------------");

			System.setProperty("https.proxyHost", properties.get("proxy.host").toString());
			System.setProperty("https.proxyPort", properties.get("proxy.port").toString());
		}

		// store P12 device type certificate in the key store

		storeDeviceTypeCertificate();

		/*
		 * decide if a new device should be registered (using certificate based authentication) or
		 * an existing device should be used instead
		 */

		String deviceId = properties.get("device.id").toString();
		Device device = null;
		if (deviceId.trim().isEmpty()) {

			System.out.println("Register a new device using certificate based authentication");

			device = registerDevice();
		}
		else {

			System.out.println("Use an existing device " + deviceId);
			System.out.println("----------------------------------------------------");

			device = new Device();
			device.setId(deviceId);
			device.setDeviceType(properties.get("device.type.id").toString());
		}

		// decide if a device certificate should be requested or it already exists in the key store

		boolean deviceCertificateExists = checkForDeviceCertificate(device);
		if (deviceCertificateExists) {
			System.out.println("Device certificate exists in key store");
			System.out.println("----------------------------------------------------");
		}
		else {
			// prepare CSR request, get device certificate from RDMS and store it in the key store

			requestAndStoreDeviceCertificate(device);
		}

		// send data to MMS on behalf of the device (using certificate based authentication)

		sendData(device);
	}

	public static void storeDeviceTypeCertificate()
	throws KeyStoreException {
		String deviceTypeId = properties.get("device.type.id").toString();
		String secret = properties.get("device.type.certificate.secret").toString();

		String path = properties.get("device.type.certificate.download.folder").toString();
		path = normalizePath(path).concat("/").concat(deviceTypeId).concat(".p12");

		keyStoreClient.storeDeviceTypeCertificate(path, secret, deviceTypeId);
	}

	public static Device registerDevice()
	throws KeyStoreException, IOException {
		String path = properties.get("iot.dms.cert.domain").toString();
		path = normalizePath(path).concat("/v2/api/devices");

		Device device = new Device();
		device.setDeviceType(properties.get("device.type.id").toString());
		device.setName("Device_".concat(UUID.randomUUID().toString()));

		String request = jsonParser.toJson(device, Device.class);

		String response = doSSLPost(path, request);

		return jsonParser.fromJson(response, Device.class);
	}

	public static boolean checkForDeviceCertificate(Device device)
	throws KeyStoreException {
		return keyStoreClient.checkForDeviceCertificate(device);
	}

	public static void requestAndStoreDeviceCertificate(Device device)
	throws KeyStoreException, IOException {
		String path = properties.get("iot.dms.cert.domain").toString();
		path = normalizePath(path).concat("/v2/api/devices/").concat(device.getId())
			.concat("/authentication");

		KeyPair keyPair = keyStoreClient.generateKeyPair();
		boolean useTwoCommonNames = Boolean
			.parseBoolean(properties.get("use.two.common.names").toString());
		PKCS10 csRequest = keyStoreClient.createCSRequest(device, keyPair, useTwoCommonNames);

		String base64 = DatatypeConverter.printBase64Binary(csRequest.getEncoded());

		Authentication authentication = new Authentication();
		authentication.setType("clientCertificate");
		authentication.setCsr(base64);

		System.out.println("Do CSR request and store device certificate in the key store");

		String request = jsonParser.toJson(authentication, Authentication.class);

		String response = doSSLPost(path, request);

		Certificate certificate = keyStoreClient
			.retrieveCertificate(jsonParser.fromJson(response, Device.class));

		keyStoreClient.storeDeviceCertificate(certificate, keyPair, device);
	}

	public static void sendData(Device device)
	throws KeyStoreException, IOException {
		String path = properties.get("iot.mms.cert.domain").toString();
		path = normalizePath(path).concat("/v1/api/http/data/").concat(device.getId());

		Message message = new SampleMessage();
		((SampleMessage) message).setSensor("Sensor_".concat(UUID.randomUUID().toString()));
		((SampleMessage) message).setValue(new Random().nextDouble());
		((SampleMessage) message)
			.setTimestamp(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

		Envelope envelope = new Envelope();
		envelope.setMode("async");
		envelope.setMessageType(properties.get("message.type.id").toString());
		envelope.setMessages(Collections.singletonList(message));

		String request = jsonParser.toJson(envelope, Envelope.class);

		System.out.println(
			"Send data to MMS on behalf of the device authenticated with the its certificate");

		doSSLPost(path, request);
	}

	private static String normalizePath(String path) {
		String slash = "/";
		if (path.endsWith(slash)) {
			return path.substring(0, path.length() - slash.length());
		}
		return path;
	}

	private static String doSSLPost(String path, String request)
	throws KeyStoreException, IOException {
		SSLSocketFactory sslSocketFactory = keyStoreClient.buildSSLSocketFactory();

		HttpsURLConnection connection = null;
		try {
			connection = httpClient.openSSLConnection(path, sslSocketFactory);
			return httpClient.doPost(connection, request);
		}
		finally {
			httpClient.closeConnection(connection);
		}
	}

	private static Properties readProperties()
	throws IOException {
		Properties properties = new Properties();

		ClassLoader classLoader = Main.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream("config.properties");

		try {
			properties.load(is);
		}
		catch (IOException e) {
			throw new IOException("Unable to read from a configuration file", e);
		}
		finally {
			closeStream(is);
		}

		return properties;
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (IOException e) {
				System.err.println("Unable to close an I/O stream");
			}
		}
	}

}