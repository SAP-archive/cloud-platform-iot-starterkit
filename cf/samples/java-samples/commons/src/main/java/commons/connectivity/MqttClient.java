package commons.connectivity;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import commons.utils.Console;
import commons.utils.Constants;

public class MqttClient
extends AbstractClient {

	private org.eclipse.paho.client.mqttv3.MqttClient client;

	private MqttConnectOptions connectOptions;

	private String clientId;

	private MqttClient(String clientId) {
		super();

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

	@Override
	public void connect(String serverUri)
	throws IOException {
		if (client != null && client.isConnected()) {
			disconnect();
		}

		Console.printText((String.format("Connect to %1$s", serverUri)));
		Console.printNewLine();

		try {
			client = new org.eclipse.paho.client.mqttv3.MqttClient(serverUri, clientId,
				new MemoryPersistence());

			client.connect(connectOptions);
		}
		catch (MqttException e) {
			String cause = e.getMessage();
			if (e.getCause() != null) {
				cause = cause.concat(" : ").concat(e.getCause().getMessage());
			}
			String errorMessage = String.format("Unable to establish an MQTT connection - %1$s",
				cause);
			throw new IOException(errorMessage, e);
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

	public <T> void publish(String topic, T payload, Class<T> clazz)
	throws IOException {
		String request = jsonParser.toJson(payload, clazz);

		Console.printText(String.format("Publish on topic '%1$s'", topic));
		Console.printNewLine();
		Console.printText(String.format("Message %1$s", request));

		MqttMessage mqttMessage = new MqttMessage(request.getBytes(Constants.DEFAULT_ENCODING));
		mqttMessage.setQos(1);

		try {
			client.publish(topic, mqttMessage);
		}
		catch (MqttException e) {
			String errorMessage = String
				.format("Unable to publish the MQTT message on topic '%1$s'", topic);
			throw new IOException(errorMessage, e);
		}
	}

	public void subscribe(String topic, final MqttMessageListener listener)
	throws IOException {
		try {
			client.subscribe(topic, new IMqttMessageListener() {

				@Override
				public void messageArrived(String topic, MqttMessage message)
				throws Exception {
					listener.onMessage(topic, message.toString());
				}

			});
		}
		catch (MqttException e) {
			String errorMessage = String.format("Unable to subscribe for the MQTT topic '%1$s'",
				topic);
			throw new IOException(errorMessage, e);
		}
	}

}