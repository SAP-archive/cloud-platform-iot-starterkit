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

public class MqttClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

	private org.eclipse.paho.client.mqttv3.MqttClient client;

	private MqttConnectOptions connectOptions;

	private String clientId;

	public MqttClient(String clientId) {
		if (clientId == null) {
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

		LOGGER.info(String.format("Connect to %1$s", destination));

		try {
			client = new org.eclipse.paho.client.mqttv3.MqttClient(destination, clientId,
				new MemoryPersistence());
			client.connect(connectOptions);
		}
		catch (MqttException e) {
			throw new IOException("Unable to establish a MQTT connection", e);
		}
	}

	public void disconnect() {
		if (client != null) {

			LOGGER.info(String.format("Disconnect from %1$s", client.getServerURI()));

			try {
				client.disconnect();
			}
			catch (MqttException e) {
				// disconnect silently
			}
		}
	}

	public void publish(String topic, String message)
	throws IOException {

		LOGGER.debug(String.format("Publish on topic '%1$s' : %2$s", topic, message));

		byte[] bytes = message.getBytes(AbstractServlet.ENCODING);
		MqttMessage mqttMessage = new MqttMessage(bytes);
		mqttMessage.setQos(1);

		try {
			client.publish(topic, mqttMessage);
		}
		catch (MqttException e) {
			throw new IOException("Unable to publish the MQTT message", e);
		}
	}

	public void subscribe(String topic, IMqttMessageListener listener)
	throws IOException {

		LOGGER.info(String.format("Subscribe for topic '%1$s'", topic));

		try {
			client.subscribe(topic, listener);
		}
		catch (MqttException e) {
			throw new IOException("Unable to subscribe for the MQTT topic", e);
		}
	}

}