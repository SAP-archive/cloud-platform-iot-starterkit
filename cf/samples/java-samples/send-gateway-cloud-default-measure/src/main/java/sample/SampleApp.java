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
import commons.model.GatewayType;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.gateway.Measure;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class SampleApp
extends AbstractCoreServiceSample {

	private GatewayCloud gatewayCloud;

	@Override
	protected String getDescription() {
		return "Send temperature measures on behalf of the sensor attached to the device" +
			" and consume them later on via the API";
	}

	@Override
	protected void run()
	throws SampleException {
		String deviceId = properties.getProperty(DEVICE_ID);
		String sensorId = properties.getProperty(SENSOR_ID);
		GatewayType gatewayType = GatewayType.fromValue(properties.getProperty(GATEWAY_TYPE));

		try {
			printSeparator();

			Gateway gateway = coreService.getOnlineGateway(gatewayType);

			printSeparator();

			Device device = getOrAddDevice(deviceId, gateway);

			printSeparator();

			/*
			 * ID '00000000-0000-0000-0000-000000000001' stands for the pre-defined Temperature
			 * capability referenced by the the default Sensor Type as a measure.
			 */
			Capability capability = coreService
				.getCapability("00000000-0000-0000-0000-000000000001");

			printSeparator();

			/*
			 * ID '0' stands for the pre-defined Sensor Type which already has some measures and
			 * commands.
			 */
			SensorType sensorType = coreService.getSensorType("0");

			Sensor sensor = getOrAddSensor(sensorId, device, sensorType);

			printSeparator();

			Authentication authentication = coreService.getAuthentication(device);

			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);
			gatewayCloud = GatewayType.REST.equals(gatewayType)
				? new GatewayCloudHttp(device, sslSocketFactory)
				: new GatewayCloudMqtt(device, sslSocketFactory);

			printSeparator();

			sendMeasures(sensor, capability);

			receiveMeasures(device, capability);
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			throw new SampleException(e.getMessage());
		}
	}

	private void sendMeasures(final Sensor sensor, final Capability capability)
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
				Measure measure = EntityFactory.buildTemperatureMeasure(sensor, capability);

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

}