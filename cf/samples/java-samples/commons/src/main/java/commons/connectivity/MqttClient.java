package commons.connectivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClient
extends AbstractClient {

	private org.eclipse.paho.client.mqttv3.MqttClient client;

	private MqttConnectOptions connectOptions;

	private String clientId;

	private String topic;

	private MqttClient(String clientId, String topic) {
		super();

		this.clientId = clientId;
		this.topic = topic;
		connectOptions = new MqttConnectOptions();
		connectOptions.setCleanSession(true);
	}

	public MqttClient(String clientId, String topic, String user, String password) {
		this(clientId, topic);

		connectOptions.setUserName(user);
		connectOptions.setPassword(password.toCharArray());
	}

	public MqttClient(String clientId, String topic, SSLSocketFactory sslSocketFactory) {
		this(clientId, topic);

		connectOptions.setSocketFactory(sslSocketFactory);
	}

	@Override
	public void connect(String destination)
	throws IOException {

		if (client != null && client.isConnected()) {
			disconnect();
		}

		System.out.println(String.format("Connect to %1$s", destination));

		try {
			client = new org.eclipse.paho.client.mqttv3.MqttClient(destination, clientId,
				new MemoryPersistence());
			client.connect(connectOptions);
		}
		catch (MqttException e) {
			throw new IOException(
				"Unable to establish a MQTT connection - " + e.getCause().getMessage(), e);
		}
	}

	@Override
	public void disconnect() {
		if (client != null) {
			try {
				client.disconnect();
			}
			catch (MqttException e) {
				// disconnect silently
			}
		}
	}

	@Override
	public <T> void send(T payload, Class<T> clazz)
	throws IOException {
		String request = jsonParser.toJson(payload);
		System.out.println(String.format("Request body %1$s", request));

		byte[] bytes = request.getBytes(StandardCharsets.UTF_8);
		MqttMessage message = new MqttMessage(bytes);
		message.setQos(1);

		try {
			client.publish(topic, message);
		}
		catch (MqttException e) {
			throw new IOException("Unable to publish the MQTT message", e);
		}
	}

}