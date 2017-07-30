package sample;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.AbstractSample;
import commons.api.CoreService;
import commons.connectivity.MqttClient;
import commons.connectivity.MqttMessageListener;
import commons.model.Authentication;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayType;
import commons.model.Sensor;
import commons.utils.Console;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class Main
extends AbstractSample {

	private CoreService coreService;

	private MqttClient mqttClient;

	public static void main(String[] args) {
		new Main().execute();
	}

	@Override
	protected String getDescription() {
		return "Send toggle valve commands to the device and listen to them on the device side";
	}

	@Override
	protected void promptProperties() {
		Console console = Console.getInstance();

		String host = properties.getProperty(IOT_HOST);
		host = console.awaitNextLine(host, "Hostname (e.g. 'test.cp.iot.sap'): ");
		properties.setProperty(IOT_HOST, host);

		String user = properties.getProperty(IOT_USER);
		user = console.awaitNextLine(user, "Username (e.g. 'root#0'): ");
		properties.setProperty(IOT_USER, user);

		properties.setProperty(GATEWAY_TYPE, GatewayType.MQTT.getValue());

		String deviceId = properties.getProperty(DEVICE_ID);
		deviceId = console.awaitNextLine(deviceId, "Device ID (e.g. '100'): ");
		properties.setProperty(DEVICE_ID, deviceId);

		String sensorId = properties.getProperty(SENSOR_ID);
		sensorId = console.awaitNextLine(sensorId, "Device sensor ID (e.g. '100'): ");
		properties.setProperty(SENSOR_ID, sensorId);

		String password = properties.getProperty(IOT_PASSWORD);
		password = console.nextPassword("Password for your user: ");
		properties.setProperty(IOT_PASSWORD, password);

		console.close();
	}

	@Override
	protected void execute() {
		String host = properties.getProperty(IOT_HOST);
		String user = properties.getProperty(IOT_USER);
		String password = properties.getProperty(IOT_PASSWORD);
		GatewayType gatewayType = GatewayType.fromValue(properties.getProperty(GATEWAY_TYPE));

		coreService = new CoreService(host, user, password);

		try {
			printSeparator();

			Gateway gateway = coreService.getOnlineGateway(gatewayType);

			printSeparator();

			Device device = getOrAddDevice(gateway);

			Sensor sensor = getOrAddDeviceSensor(device);

			printSeparator();

			Authentication authentication = coreService.getAuthentication(device);

			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);
			String clientId = device.getPhysicalAddress();
			mqttClient = new MqttClient(clientId, sslSocketFactory);

			printSeparator();

			listenCommands(device);

			sendCommands(device, sensor);

			disconnect();
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			printError(String.format("Execution failure: %1$s", e.getMessage()));
			System.exit(1);
		}
	}

	private Device getOrAddDevice(Gateway gateway)
	throws IOException {
		String deviceId = properties.getProperty(DEVICE_ID);

		Device device;
		try {
			device = coreService.getOnlineDevice(deviceId, gateway);
		}
		catch (IOException | IllegalStateException e) {
			printWarning(e.getMessage());

			printSeparator();

			Device deviceTemplate = EntityFactory.buildDevice(gateway);
			device = coreService.addDevice(deviceTemplate);

			printNewLine();
			printProperty(DEVICE_ID, device.getId());
		}

		return device;
	}

	private Sensor getOrAddDeviceSensor(Device device)
	throws IOException {
		String sensorId = properties.getProperty(SENSOR_ID);

		Sensor sensor = null;
		Sensor[] sensors = device.getSensors();
		if (sensors != null) {
			for (int i = 0; i < sensors.length; i++) {
				Sensor nextSensor = sensors[i];
				if (nextSensor.getId().equals(sensorId)) {
					sensor = nextSensor;
					break;
				}
			}
		}
		if (sensor == null) {
			printWarning(String.format("No sensor '%1$s' is attached to the device '%2$s'",
				sensorId, device.getId()));

			printSeparator();

			Sensor sensorTemplate = EntityFactory.buildSensor(device);
			sensor = coreService.addSensor(sensorTemplate);

			printNewLine();
			printProperty(SENSOR_ID, sensor.getId());
		}

		return sensor;
	}

	private void listenCommands(Device device)
	throws IOException {
		String host = properties.getProperty(IOT_HOST);
		String topic = String.format("commands/%1$s", device.getPhysicalAddress());
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

	private void sendCommands(final Device device, final Sensor sensor)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Command command = EntityFactory.buildToggleValveCommand(sensor);

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

}