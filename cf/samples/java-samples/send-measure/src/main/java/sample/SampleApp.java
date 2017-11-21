package sample;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.AbstractCoreServiceSample;
import commons.SampleException;
import commons.api.GatewayCloud;
import commons.api.GatewayCloudHttp;
import commons.api.GatewayCloudMqtt;
import commons.model.Authentication;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.gateway.Measure;
import commons.utils.Console;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class SampleApp
extends AbstractCoreServiceSample {

	private GatewayCloud gatewayCloud;

	@Override
	protected String getDescription() {
		return "Send ambient measures on behalf of the device sensor" +
			" and consume them later on via the API";
	}

	@Override
	protected void run()
	throws SampleException {
		String deviceId = properties.getProperty(DEVICE_ID);
		String sensorId = properties.getProperty(SENSOR_ID);
		GatewayProtocol gatewayProtocol = GatewayProtocol
			.fromValue(properties.getProperty(GATEWAY_PROTOCOL_ID));

		try {
			Console.printSeparator();

			Gateway gateway = coreService.getOnlineCloudGateway(gatewayProtocol);

			Console.printSeparator();

			Device device = getOrAddDevice(deviceId, gateway);

			Console.printSeparator();

			Capability measureCapability = getOrAddCapability(
				EntityFactory.buildAmbientCapability());

			Console.printSeparator();

			Capability commandCapability = getOrAddCapability(
				EntityFactory.buildSwitchCapability());

			Console.printSeparator();

			SensorType sensorType = getOrAddSensorType(measureCapability, commandCapability);

			Sensor sensor = getOrAddSensor(sensorId, device, sensorType);

			Console.printSeparator();

			Authentication authentication = coreService.getAuthentication(device);

			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);

			switch (gatewayProtocol) {
			case MQTT:
				gatewayCloud = new GatewayCloudMqtt(device, sslSocketFactory);
				break;
			case REST:
			default:
				gatewayCloud = new GatewayCloudHttp(device, sslSocketFactory);
				break;
			}

			Console.printSeparator();

			sendAmbientMeasures(sensor, measureCapability);

			receiveAmbientMeasures(device, measureCapability);
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			throw new SampleException(e.getMessage());
		}
	}

	private void sendAmbientMeasures(final Sensor sensor, final Capability capability)
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
				Measure measure = EntityFactory.buildAmbientMeasure(sensor, capability);

				try {
					gatewayCloud.sendMeasure(measure);
				}
				catch (IOException e) {
					Console.printError(e.getMessage());
				}
				finally {
					Console.printSeparator();
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

	private void receiveAmbientMeasures(final Device device, final Capability capability)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					coreService.getLatestMeasures(device, capability, 25);
				}
				catch (IOException e) {
					Console.printError(e.getMessage());
				}
				finally {
					Console.printSeparator();
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
			coreService.shutdown();
		}
	}

}