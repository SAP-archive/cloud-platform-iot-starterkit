package com.sap.iot.starterkit.mqtt.ingest.connect;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.iot.starterkit.mqtt.ingest.AbstractServlet;

/**
 * A proxy (wrapper) for {@link org.eclipse.paho.client.mqttv3.MqttClient} Paho client
 */
public class MqttClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

	private org.eclipse.paho.client.mqttv3.MqttClient client;

	private MqttConnectOptions connectOptions;

	private String clientId;

	public MqttClient(String clientId) {
		if (clientId == null || clientId.trim().isEmpty()) {
			clientId = org.eclipse.paho.client.mqttv3.MqttClient.generateClientId();
		}
		this.clientId = clientId;
		connectOptions = new MqttConnectOptions();
		connectOptions.setCleanSession(true);
	}

	public MqttClient(String clientId, String user, String password) {
		this(clientId);
		connectOptions.setUserName(user);
		connectOptions.setPassword(password.toCharArray());
	}

	public MqttClient(String clientId, SSLSocketFactory sslSocketFactory) {
		this(clientId);
		connectOptions.setSocketFactory(sslSocketFactory);
	}

	public void connect(String destination)
	throws IOException {

		if (client != null && client.isConnected()) {
			disconnect();
		}

		LOGGER.info(String.format("Connecting to %1$s ...", destination));

		try {
			client = new org.eclipse.paho.client.mqttv3.MqttClient(destination, clientId,
				new MemoryPersistence());
			client.connect(connectOptions);
		}
		catch (MqttException e) {
			throw new IOException("Unable to establish a MQTT connection", e);
		}

		LOGGER.info(String.format("Connected to %1$s", destination));
	}

	public void disconnect() {
		if (client != null) {

			LOGGER.info(String.format("Disconnecting from %1$s ...", client.getServerURI()));

			try {
				client.disconnect();
				if (client.isConnected()) {
					client.disconnectForcibly();
				}
			}
			catch (MqttException e) {
				// disconnect silently
			}
			finally {
				try {
					client.close();
				}
				catch (MqttException e) {
					// close silently
				}

				LOGGER.info(String.format("Disconnected from %1$s", client.getServerURI()));
			}
		}
	}

	public void publish(String topic, String message)
	throws IOException {

		LOGGER.debug(String.format("Publishing on topic '%1$s' : %2$s", topic, message));

		byte[] bytes = message.getBytes(AbstractServlet.ENCODING);
		MqttMessage mqttMessage = new MqttMessage(bytes);
		mqttMessage.setQos(1);

		try {
			client.publish(topic, mqttMessage);

			LOGGER.debug(String.format("Published on topic '%1$s' : %2$s", topic, message));
		}
		catch (MqttException e) {
			throw new IOException("Unable to publish the MQTT message", e);
		}
	}

	public void subscribe(String topic, IMqttMessageListener listener)
	throws IOException {

		LOGGER.info(String.format("Subscribing for topic '%1$s' ...", topic));

		try {
			client.subscribe(topic, listener);

			LOGGER.info(String.format("Subscribed for topic '%1$s'", topic));
		}
		catch (MqttException e) {
			throw new IOException("Unable to subscribe for the MQTT topic", e);
		}
	}

}