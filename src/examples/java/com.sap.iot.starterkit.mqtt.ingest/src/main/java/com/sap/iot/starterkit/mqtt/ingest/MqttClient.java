package com.sap.iot.starterkit.mqtt.ingest;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.iot.starterkit.mqtt.ingest.util.Constant;

public class MqttClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

	private String destination;

	private String clientId;

	private org.eclipse.paho.client.mqttv3.MqttClient client;

	public MqttClient(String destination, String clientId) {
		this.destination = destination;
		this.clientId = clientId;
	}

	public void connect(String user, String password)
	throws IOException {
		MqttConnectOptions connectOptions = new MqttConnectOptions();
		connectOptions.setUserName(user);
		connectOptions.setPassword(password.toCharArray());
		connectOptions.setCleanSession(true);

		connect(destination, connectOptions);
	}

	public void disconnect() {
		if (client != null) {
			try {
				client.disconnect();

				LOGGER.info(String.format("Disconnected from '%1$s' topic", destination));
			}
			catch (MqttException e) {
				LOGGER.error("Unable to properly close the MQTT connection", e);
			}
		}
	}

	public void subscribe(String topic, IMqttMessageListener listener)
	throws IOException {
		try {
			client.subscribe(topic, listener);

			LOGGER.info(String.format("Subscribed for '%1$s' topic", topic));
		}
		catch (MqttException e) {
			throw new IOException("Unable to subscribe for the MQTT topic", e);
		}
	}

	public void publish(String topic, String message)
	throws IOException {
		LOGGER.info(String.format("Publishing on topic '%1$s': %2$s", topic, message));

		byte[] bytes = message.getBytes(Constant.ENCODING);
		MqttMessage mqttMessage = new MqttMessage(bytes);
		mqttMessage.setQos(1);
		try {
			client.publish(topic, mqttMessage);
		}
		catch (MqttException e) {
			throw new IOException("Unable to publish the MQTT message", e);
		}
	}

	private void connect(String destination, MqttConnectOptions connectOptions)
	throws IOException {
		if (client != null && client.isConnected()) {
			disconnect();
		}

		try {
			client = new org.eclipse.paho.client.mqttv3.MqttClient(destination, clientId,
				new MemoryPersistence());
			client.connect(connectOptions);

			LOGGER.info(String.format("Connected to %1$s", destination));
		}
		catch (MqttException e) {
			throw new IOException("Unable to establish a MQTT connection", e);
		}
	}

}