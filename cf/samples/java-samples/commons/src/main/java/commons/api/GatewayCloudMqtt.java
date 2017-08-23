package commons.api;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import commons.connectivity.MqttClient;
import commons.model.Device;

public class GatewayCloudMqtt
implements GatewayCloud {

	private MqttClient mqttClient;

	public GatewayCloudMqtt(Device device, SSLSocketFactory sslSocketFactory) {
		String physicalAddress = device.getAlternateId();
		String clientId = physicalAddress;
		String topic = String.format("measures/%1$s", physicalAddress);

		mqttClient = new MqttClient(clientId, topic, sslSocketFactory);
	}

	@Override
	public void connect(String host)
	throws IOException {
		host = String.format("ssl://%1$s:8883", host);

		mqttClient.connect(host);
	}

	@Override
	public void disconnect() {
		mqttClient.disconnect();
	}

	@Override
	public <T> void send(T payload, Class<T> clazz)
	throws IOException {
		mqttClient.send(payload, clazz);
	}

}
