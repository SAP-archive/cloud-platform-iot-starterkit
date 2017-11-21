package commons.api;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLSocketFactory;

import commons.connectivity.MqttClient;
import commons.connectivity.MqttMessageListener;
import commons.model.Device;
import commons.model.gateway.Measure;
import commons.utils.Console;

public class GatewayCloudMqtt
implements GatewayCloud {

	private MqttClient mqttClient;

	private String upstreamTopic;

	private String downstreamTopic;

	private ExecutorService executor;

	public GatewayCloudMqtt(Device device, SSLSocketFactory sslSocketFactory) {
		String deviceAlternateId = device.getAlternateId();
		String clientId = deviceAlternateId;

		upstreamTopic = String.format("measures/%1$s", deviceAlternateId);
		downstreamTopic = String.format("commands/%1$s", deviceAlternateId);

		mqttClient = new MqttClient(clientId, sslSocketFactory);
	}

	@Override
	public void connect(String host)
	throws IOException {
		host = String.format("ssl://%1$s:8883", host);

		mqttClient.connect(host);
	}

	@Override
	public void disconnect() {
		if (executor != null) {
			executor.shutdown();
		}
		mqttClient.disconnect();
	}

	@Override
	public void sendMeasure(Measure measure)
	throws IOException {
		mqttClient.publish(upstreamTopic, measure, Measure.class);
	}

	@Override
	public void listenCommands()
	throws IOException {
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					mqttClient.subscribe(downstreamTopic, new MqttMessageListener() {

						@Override
						public void onMessage(String topic, String message) {
							Console.printText(String.format("Message on topic '%1$s'", topic));
							Console.printNewLine();
							Console.printText(String.format("Message %1$s", message));
							Console.printSeparator();
						}

					});
				}
				catch (IOException e) {
					Console.printError(e.getMessage());
				}
			}

		});
	}

}