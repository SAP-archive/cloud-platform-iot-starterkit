package sample;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.AbstractCoreServiceSample;
import commons.SampleException;
import commons.connectivity.MqttClient;
import commons.connectivity.MqttMessageListener;
import commons.model.Authentication;
import commons.model.Capability;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.utils.Console;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class SampleApp
extends AbstractCoreServiceSample {

	private MqttClient mqttClient;

	@Override
	protected String getDescription() {
		return "Send switch commands to the device and listen to them on the device side";
	}

	@Override
	protected void promptProperties() {
		Console console = Console.getInstance();

		String host = properties.getProperty(IOT_HOST);
		host = console.awaitNextLine(host, "Hostname (e.g. 'test.cp.iot.sap'): ");
		properties.setProperty(IOT_HOST, host);

		String user = properties.getProperty(IOT_USER);
		user = console.awaitNextLine(user, "Username (e.g. 'root'): ");
		properties.setProperty(IOT_USER, user);

		properties.setProperty(GATEWAY_PROTOCOL_ID, GatewayProtocol.MQTT.getValue());

		String deviceId = properties.getProperty(DEVICE_ID);
		deviceId = console.awaitNextLine(deviceId, "Device ID (e.g. '100'): ");
		properties.setProperty(DEVICE_ID, deviceId);

		String sensorId = properties.getProperty(SENSOR_ID);
		sensorId = console.awaitNextLine(sensorId, "Sensor ID (e.g. '100'): ");
		properties.setProperty(SENSOR_ID, sensorId);

		String password = properties.getProperty(IOT_PASSWORD);
		password = console.nextPassword("Password for your user: ");
		properties.setProperty(IOT_PASSWORD, password);

		console.close();
	}

	@Override
	protected void run()
	throws SampleException {
		String deviceId = properties.getProperty(DEVICE_ID);
		String sensorId = properties.getProperty(SENSOR_ID);
		GatewayProtocol gatewayProtocol = GatewayProtocol
			.fromValue(properties.getProperty(GATEWAY_PROTOCOL_ID));

		try {
			printSeparator();

			Gateway gateway = coreService.getOnlineCloudGateway(gatewayProtocol);

			printSeparator();

			Device device = getOrAddDevice(deviceId, gateway);

			printSeparator();

			Capability measureCapability = getOrAddCapability(
				EntityFactory.buildAmbientCapability());

			printSeparator();

			Capability commandCapability = getOrAddCapability(
				EntityFactory.buildSwitchCapability());

			printSeparator();

			SensorType sensorType = getOrAddSensorType(measureCapability, commandCapability);

			Sensor sensor = getOrAddSensor(sensorId, device, sensorType);

			printSeparator();

			Authentication authentication = coreService.getAuthentication(device);

			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);
			String clientId = device.getAlternateId();
			mqttClient = new MqttClient(clientId, sslSocketFactory);

			printSeparator();

			listenCommands(device);

			sendCommands(device, sensor, commandCapability);
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			throw new SampleException(e.getMessage());
		}
	}

	private void listenCommands(Device device)
	throws IOException {
		String host = properties.getProperty(IOT_HOST);
		String topic = String.format("commands/%1$s", device.getAlternateId());
		String destination = String.format("ssl://%1$s:8883", host);

		try {
			mqttClient.connect(destination);
			printSeparator();
			mqttClient.subscribe(topic, new MqttMessageListener() {

				@Override
				public void onMessage(String topic, String message) {
					System.out.println(String.format("Message on topic '%1$s'", topic));
					printNewLine();
					System.out.println(String.format("Message %1$s", message));
					printSeparator();
				}

			});
		}
		catch (IOException e) {
			throw new IOException("Unable to subscribe over MQTT", e);
		}

		disconnect();
	}

	private void sendCommands(final Device device, final Sensor sensor, final Capability capability)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Command command = EntityFactory.buildSwitchCommand(sensor, capability);

				try {
					coreService.sendCommand(command, device);
				}
				catch (IOException e) {
					// do nothing
				}
				finally {
					printSeparator();
				}
			}

		}, 0, 1000, TimeUnit.MILLISECONDS);

		try {
			executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			throw new IOException("Interrupted exception", e);
		}
		finally {
			executor.shutdown();
		}
	}

	private void disconnect()
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				mqttClient.disconnect();
			}

		}, 20000, TimeUnit.MILLISECONDS);

		try {
			executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			throw new IOException("Interrupted exception", e);
		}
		finally {
			executor.shutdown();
		}
	}

}