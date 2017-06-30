package mqtt.client;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * MQTT over WebSocket sample client for the HCP IoT Services. More information can be found here:
 * https://help.hana.ondemand.com/iot/frameset.htm?56d02092904346c1a605713021d2f875.html
 * 
 *
 */
public class IoTServicesSample {

	public static void main(String[] args) {
		/*
		 * Enter MMS configuration here.
		 */
		String mmsHost = "iotmms<enter hcp account id>.hanatrial.ondemand.com";
		String deviceId = "<enter device id>";
		String oauthToken = "<enter oauth token>";
		String msg = "{\"mode\":\"async\", \"messageType\":\"<enter message type id>\", \"messages\": [{<enter message>}]}";

		try {
			String dataTopic = "iot/data/" + deviceId;
			String logTopic = "iot/log/" + deviceId;
			String broker = "wss://" + mmsHost + "/com.sap.iotservices.mms/v1/api/ws/mqtt";

			MqttClient sampleClient = new MqttClient(broker, deviceId, new MemoryPersistence());
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(deviceId);
			connOpts.setPassword(oauthToken.toCharArray());

			/*-
			 * When connecting through wss with a broker that uses a self-signed certificate or a
			 * certificate that is not trusted by default, there are two options.
			 * 
			 * 1. Disable host verification. This should only be used for testing. It is not
			 * recommended in productive environments.
			 * 
			 * connOpts.setSocketFactory(getTrustAllSocketFactory());
			 * 
			 * 
			 * 2. Add the certificate to your keystore. The default keystore is located in the JRE
			 * in <jre home>/lib/security/cacerts. The certificate can be added with
			 * 
			 * "keytool -import -alias my.broker.com -keystore cacerts -file my.broker.com.pem".
			 * 
			 * It is also possible to point to a custom keystore:
			 * 
			 * Properties props = new Properties(); 
			 * props.setProperty("com.ibm.ssl.trustStore","my.cacerts"); 
			 * connOpts.setSSLProperties(props);
			 * 
			 */

			connOpts.setCleanSession(true);
			System.out.println("Connecting to broker: " + broker);
			sampleClient.connect(connOpts);
			System.out.println("Cient connected");
			System.out.println("Subscribing to topic: " + logTopic);
			sampleClient.subscribe(logTopic, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message)
				throws Exception {
					System.out.println("Received message on topic \"" + topic + "\": " + message);
				}
			});
			System.out.println("Publishing message on topic \"" + dataTopic + "\": " + msg);
			MqttMessage message = new MqttMessage(msg.getBytes());
			message.setQos(1);
			sampleClient.publish(dataTopic, message);
			System.out.println("Message published");
			sampleClient.disconnect();
			System.out.println("Disconnected");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Do not use in production! This trust manager trusts whatever certificate is provided.
	 * 
	 * @return Socket factory that excepts all certificates.
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static SSLSocketFactory getTrustAllSocketFactory()
	throws GeneralSecurityException {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[] { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
				// Not implemented
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
				// Not implemented
			}
		} }, new java.security.SecureRandom());
		return sc.getSocketFactory();
	}
}