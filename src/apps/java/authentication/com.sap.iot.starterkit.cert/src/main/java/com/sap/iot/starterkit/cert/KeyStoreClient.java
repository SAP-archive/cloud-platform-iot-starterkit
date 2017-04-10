package com.sap.iot.starterkit.cert;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import com.sap.iot.starterkit.cert.type.Device;

import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

@SuppressWarnings("restriction")
public class KeyStoreClient {

	private static final char[] SSL_KEYSTORE_SECRET = "hkRPusjglo".toCharArray();

	private static final String JDK_TRUSTSTORE_PATH = System.getProperty("java.home") +
		"/lib/security/cacerts";

	private static final Pattern DEVICEID_PATTERN_SINGLE = Pattern.compile("(deviceId\\:)(.*)");
	private static final Pattern TENANTID_PATTERN_SINGLE = Pattern.compile("(tenantId\\:)(.*)");

	private KeyStore keyStore;

	private String keyStorePath;

	public KeyStoreClient()
	throws KeyStoreException {
		ClassLoader classLoader = KeyStoreClient.class.getClassLoader();
		keyStorePath = classLoader.getResource("").getPath() + "/" + "keystore.p12";

		keyStore = load("PKCS12", keyStorePath, SSL_KEYSTORE_SECRET);
	}

	/**
	 * Decodes the device type P12 certificate and stores X509 in the key store
	 */
	public void storeDeviceTypeCertificate(String path, String secret, String deviceTypeId)
	throws KeyStoreException {
		char[] secretAsChars = secret.toCharArray();

		// X509 certificate
		Certificate certificate = null;
		// RSA private key
		Key key = null;

		try {
			KeyStore tempKeyStore = load("PKCS12", path, secretAsChars);

			// should be equal to "1" when we decode X509 certificate with native Java
			String alias = "1";

			key = tempKeyStore.getKey(alias, secretAsChars);
			certificate = tempKeyStore.getCertificate(alias);
		}
		catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new KeyStoreException("Unable to get X.509 certificate from P12 file", e);
		}

		storeCertificate("private", certificate, key);
	}

	public void storeDeviceCertificate(Certificate certificate, KeyPair keyPair, Device device)
	throws KeyStoreException {
		storeCertificate(device.getId(), certificate, keyPair.getPrivate());

		setPrivateCertificate(certificate, keyPair.getPrivate());
	}

	/**
	 * Retrieves device certificate out of the RDMS response (Device JSON object)
	 */
	public Certificate retrieveCertificate(Device device)
	throws KeyStoreException {
		byte[] bytes = DatatypeConverter
			.parseBase64Binary(device.getAuthentication().getX509Certificate());
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);

		Certificate certificate = null;
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			certificate = certificateFactory.generateCertificate(is);
		}
		catch (CertificateException e) {
			throw new KeyStoreException("Unable to get X.509 certificate from RDMS response", e);
		}
		finally {
			closeStream(is);
		}

		return certificate;
	}

	/**
	 * Check for a device certificate in the key store
	 */
	public boolean checkForDeviceCertificate(Device device)
	throws KeyStoreException {
		if (keyStore == null || !keyStore.containsAlias(device.getId()) ||
			!keyStore.isKeyEntry(device.getId())) {
			return false;
		}

		X509Certificate deviceCertificate = (X509Certificate) keyStore
			.getCertificate(device.getId());
		Key deviceCertificateKey;
		try {
			deviceCertificateKey = keyStore.getKey(device.getId(), SSL_KEYSTORE_SECRET);
		}
		catch (UnrecoverableKeyException | NoSuchAlgorithmException e1) {
			System.err.println("Device certificate private key could not be retrieved.");
			return false;
		}
		try {
			deviceCertificate.checkValidity();
		}
		catch (CertificateExpiredException | CertificateNotYetValidException e) {
			System.err.println("Device certificate expired or not yet valid");
			return false;
		}

		Principal principal = deviceCertificate.getSubjectDN();

		String[] name = getPrincipalAttributeValue(principal, "CN", "");

		String deviceId = extractMatchingValue(name, DEVICEID_PATTERN_SINGLE);

		if (device.getId().equals(deviceId)) {

			// device certificate is in the key store, set it as private for SSL connection

			setPrivateCertificate(deviceCertificate, deviceCertificateKey);

			return true;
		}

		return false;
	}

	/**
	 * Creates a Certificate Signing Request and signs it with RSA private key
	 */
	public PKCS10 createCSRequest(Device device, KeyPair keyPair, boolean twoCommonNames)
	throws KeyStoreException {
		X500Name x500Name = createX500NameForDevice(device, twoCommonNames);

		PKCS10 request = null;
		try {
			request = new PKCS10(keyPair.getPublic());
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(keyPair.getPrivate());
			request.encodeAndSign(x500Name, signature);
		}
		catch (Exception e) {
			throw new KeyStoreException("Unable to create CSR request", e);
		}

		return request;
	}

	/**
	 * Generates a PKI (a public and private key for RSA)
	 */
	public KeyPair generateKeyPair()
	throws KeyStoreException {
		KeyPair keyPair;
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			keyPair = generator.generateKeyPair();
		}
		catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException("Unable to generate PKI", e);
		}
		return keyPair;
	}

	/**
	 * Builds SSL Socket Factory for HTTP communication
	 */
	public SSLSocketFactory buildSSLSocketFactory()
	throws KeyStoreException {
		SSLContext sslContext = null;
		try {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			keyManagerFactory.init(keyStore, SSL_KEYSTORE_SECRET);

			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
			TrustManager[] trustManagers = new TrustManager[] {
				createTrustManagerFromDefaultJDKTrustStore() };

			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);

			SSLContext.setDefault(sslContext);
		}
		catch (GeneralSecurityException | IOException e) {
			throw new KeyStoreException("Unable to instantiate SSL context", e);
		}
		return sslContext.getSocketFactory();
	}

	private X509TrustManager createTrustManager(KeyStore truststore)
	throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory tmfactory = TrustManagerFactory
			.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(truststore);
		TrustManager[] trustManagers = tmfactory.getTrustManagers();
		if ((trustManagers != null) && (trustManagers.length > 0)) {
			return (X509TrustManager) trustManagers[0];
		}
		return null;
	}

	private X509TrustManager createTrustManagerFromDefaultJDKTrustStore()
	throws IOException, GeneralSecurityException {
		KeyStore jdkTrustStore = load("jks", JDK_TRUSTSTORE_PATH, null);
		return createTrustManager(jdkTrustStore);
	}

	private void storeCertificate(String alias, Certificate certificate, Key key)
	throws KeyStoreException {
		keyStore.setKeyEntry(alias, key, SSL_KEYSTORE_SECRET, new Certificate[] { certificate });

		store();
	}

	/**
	 * Set given certificate as a private one for SSL connectivity
	 */
	private void setPrivateCertificate(Certificate certificate, Key key)
	throws KeyStoreException {
		String alias = "private";

		keyStore.deleteEntry(alias);
		keyStore.setKeyEntry(alias, key, SSL_KEYSTORE_SECRET, new Certificate[] { certificate });

		// necessary for the correct TLS-Handshake
		store();
		keyStore = load("PKCS12", keyStorePath, SSL_KEYSTORE_SECRET);

	}

	private KeyStore load(String type, String path, char[] secret)
	throws KeyStoreException {
		KeyStore keyStore = KeyStore.getInstance(type);
		InputStream is = null;
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				try {
					is = new FileInputStream(file);
				}
				catch (FileNotFoundException e) {
					throw new KeyStoreException("Unable to open P12 key store file", e);
				}
			}
		}
		try {
			keyStore.load(is, secret);
		}
		catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeyStoreException("Unable to load a key tore from P12 file", e);
		}
		return keyStore;
	}

	/**
	 * Creates a subject name using the device id and other values from the device type certificate
	 */
	private X500Name createX500NameForDevice(Device device, boolean twoCommonNames)
	throws KeyStoreException {
		X509Certificate deviceTypeCertificate = (X509Certificate) keyStore
			.getCertificate("private");
		Principal principal = deviceTypeCertificate.getSubjectDN();

		String country = getPrincipalAttributeValue(principal, "C", "DE")[0];
		String organization = getPrincipalAttributeValue(principal, "O", "SAP Trust Community")[0];
		String unit = getPrincipalAttributeValue(principal, "OU", "IoT Services")[0];
		String[] name = getPrincipalAttributeValue(principal, "CN", "");

		String tenantId = extractMatchingValue(name, TENANTID_PATTERN_SINGLE);

		String commonName1 = "deviceId:" + device.getId();
		String commonName2 = "tenantId:" + tenantId;

		String newName = null;
		if (twoCommonNames) {
			newName = "CN=" + commonName1 + ",CN=" + commonName2 + ",OU=" + unit + ",O=" +
				organization + ",C=" + country;
		}
		else {
			newName = "CN=" + commonName1 + "|" + commonName2 + ",OU=" + unit + ",O=" +
				organization + ",C=" + country;
		}

		X500Name x500Name = null;
		try {
			x500Name = new X500Name(newName);
		}
		catch (IOException e) {
			throw new KeyStoreException("Unable to create X500 name for a device", e);
		}

		return x500Name;
	}

	/**
	 * Retrieves attributes from a common name or gives back a default value
	 */
	private String[] getPrincipalAttributeValue(Principal principal, String attributeName,
		String defaultValue) {
		ArrayList<String> attributeEntries = new ArrayList<String>();
		String[] principleAttributes = principal.toString().split(",");
		for (String attribute : principleAttributes) {
			if (attribute.contains(attributeName + "=")) {
				attributeEntries.add(attribute.split("=")[1]);
			}
		}
		if (attributeEntries.isEmpty()) {
			return new String[] { defaultValue };
		}
		else {
			return attributeEntries.toArray(new String[attributeEntries.size()]);
		}
	}

	private void store()
	throws KeyStoreException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(keyStorePath);
		}
		catch (FileNotFoundException e) {
			throw new KeyStoreException("Unable to find P12 keystore file", e);
		}

		try {
			keyStore.store(os, SSL_KEYSTORE_SECRET);
		}
		catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeyStoreException("Unable to store the key store into output stream", e);
		}
		finally {
			closeStream(os);
		}
	}

	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (IOException e) {
				System.err.println("Unable to close an I/O stream");
			}
		}
	}

	private String extractMatchingValue(String[] commonName, Pattern pattern) {
		String Id = null;
		for (String element : commonName) {
			Matcher matcher = pattern.matcher(element);
			if (matcher.find()) {
				Id = matcher.group(2);
			}
		}
		return Id;
	}

}