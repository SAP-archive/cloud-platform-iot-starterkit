package sample;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.AbstractCoreServiceSample;
import commons.api.GatewayCloud;
import commons.api.GatewayCloudHttp;
import commons.api.GatewayCloudMqtt;
import commons.model.Authentication;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayType;
import commons.model.Sensor;
import commons.model.gateway.Measure;
import commons.utils.Console;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class Main
extends AbstractCoreServiceSample {

	private GatewayCloud gatewayCloud;

	public static void main(String[] args) {
		new Main().execute();
	}

	@Override
	protected String getDescription() {
		return "Send temperature measures on behalf of the sensor attached to the device" +
			" and consume them later on via the API";
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

		String gatewayType = properties.getProperty(GATEWAY_TYPE);
		gatewayType = console.awaitNextLine(gatewayType, "Gateway Type ('rest' or 'mqtt'): ");
		properties.setProperty(GATEWAY_TYPE, GatewayType.fromValue(gatewayType).getValue());

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
		GatewayType gatewayType = GatewayType.fromValue(properties.getProperty(GATEWAY_TYPE));

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
			gatewayCloud = GatewayType.REST.equals(gatewayType)
				? new GatewayCloudHttp(device, sslSocketFactory)
				: new GatewayCloudMqtt(device, sslSocketFactory);

			printSeparator();

			sendMeasures(sensor);

			receiveMeasures(device);
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			printError(String.format("Execution failure: %1$s", e.getMessage()));
			System.exit(1);
		}
	}

	private void sendMeasures(final Sensor sensor)
	throws IOException {
		String host = properties.getProperty(IOT_HOST);

		try {
			gatewayCloud.connect(host);
		}
		catch (IOException e) {
			throw new IOException("Unable to connect to the Gateway Cloud", e);
		}

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Measure measure = EntityFactory.buildTemperatureMeasure(sensor);

				try {
					gatewayCloud.send(measure, Measure.class);
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
			gatewayCloud.disconnect();
		}
	}

	private void receiveMeasures(final Device device)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					coreService.getLatestMeasures(device);
				}
				catch (IOException e) {
					// do nothing
				}
				finally {
					printSeparator();
				}
			}

		}, 5000, TimeUnit.MILLISECONDS);

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