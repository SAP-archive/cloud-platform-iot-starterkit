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
import commons.model.Authentication;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayType;
import commons.model.Sensor;
import commons.utils.Console;
import commons.utils.Constants;
import commons.utils.ObjectFactory;
import commons.utils.SecurityUtil;

/**
 * Main entry point of the sample application
 */
public class Main
extends AbstractSample {

	private CoreService coreService;

	public static void main(String[] args) {
		new Main().execute();
	}

	@Override
	protected String getDescription() {
		return "Send and listen to default device commands";
	}

	@Override
	protected void promptProperties() {
		Console console = Console.getInstance();

		String host = properties.getProperty(Constants.IOT_HOST);
		host = console.awaitNextLine(host, "Hostname (e.g. 'test.cp.iot.sap'): ");
		properties.setProperty(Constants.IOT_HOST, host);

		String user = properties.getProperty(Constants.IOT_USER);
		user = console.awaitNextLine(user, "Username (e.g. 'root#0'): ");
		properties.setProperty(Constants.IOT_USER, user);

		properties.setProperty(Constants.GATEWAY_TYPE, GatewayType.MQTT.getValue());

		String physicalAddress = properties.getProperty(Constants.DEVICE_ID);
		physicalAddress = console.awaitNextLine(physicalAddress, "Device ID (e.g. '100'): ");
		properties.setProperty(Constants.DEVICE_ID, physicalAddress);

		String password = properties.getProperty(Constants.IOT_PASSWORD);
		password = console.nextPassword("Password for your username: ");
		properties.setProperty(Constants.IOT_PASSWORD, password);

		console.close();
	}

	@Override
	protected void execute() {
		String host = properties.getProperty(Constants.IOT_HOST);
		String user = properties.getProperty(Constants.IOT_USER);
		String password = properties.getProperty(Constants.IOT_PASSWORD);
		String deviceId = properties.getProperty(Constants.DEVICE_ID);
		GatewayType gatewayType = GatewayType
			.fromValue(properties.getProperty(Constants.GATEWAY_TYPE));

		coreService = new CoreService(host, user, password);

		try {
			System.out.println(Constants.SEPARATOR);
			Gateway gateway = coreService.getOnlineGateway(gatewayType);

			System.out.println(Constants.SEPARATOR);
			Device device = coreService.getOrAddDevice(deviceId, gateway);

			System.out.println(Constants.SEPARATOR);
			Authentication authentication = coreService.getAuthentication(device);
			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);

			System.out.println(Constants.SEPARATOR);
			listenCommands(device, sslSocketFactory);

			System.out.println(Constants.SEPARATOR);
			sendCommands(device);
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			System.err.println(String.format("[ERROR] Execution failure: %1$s", e.getMessage()));
			System.exit(1);
		}
	}

	/**
	 * Listens to incoming commands coming from the Gateway MQTT.
	 */
	private void listenCommands(Device device, SSLSocketFactory sslSocketFactory)
	throws IOException {
		String host = properties.getProperty(Constants.IOT_HOST);
		String clientId = device.getPhysicalAddress();
		String topic = String.format("commands/%1$s", device.getPhysicalAddress());
		String destination = String.format("ssl://%1$s:8883", host);

		final MqttClient mqttClient = new MqttClient(clientId, sslSocketFactory);
		try {
			mqttClient.connect(destination);
			mqttClient.subscribe(topic);
		}
		catch (IOException e) {
			throw new IOException("Unable to subscribe over MQTT", e);
		}

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				mqttClient.disconnect();
			}

		}, 20000, TimeUnit.MILLISECONDS);

		executor.shutdown();
	}

	/**
	 * Sends random toggle valve commands to the device. Commands are being sent each second during
	 * the 5 seconds time frame.
	 */
	private void sendCommands(final Device device)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Sensor sensor = device.getSensors()[0];
				Command command = ObjectFactory.buildToggleValveCommand();
				command.setSensorId(sensor.getId());

				try {
					coreService.sendCommand(device, command);
				}
				catch (IOException e) {
					// do nothing
				}
				finally {
					System.out.println(Constants.SEPARATOR);
				}
			}

		}, 0l, 1000, TimeUnit.MILLISECONDS);

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