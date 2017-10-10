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
import commons.model.CapabilityType;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayProtocol;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.SensorTypeCapability;
import commons.model.gateway.Measure;
import commons.utils.EntityFactory;
import commons.utils.SecurityUtil;

public class SampleApp
extends AbstractCoreServiceSample {

	private GatewayCloud gatewayCloud;

	@Override
	protected String getDescription() {
		return "Send humidity measures on behalf of the sensor attached to the device" +
			" and consume them later on via the API";
	}

	@Override
	protected void run()
	throws SampleException {
		String deviceId = properties.getProperty(DEVICE_ID);
		String sensorId = properties.getProperty(SENSOR_ID);
		GatewayProtocol gatewayType = GatewayProtocol.fromValue(properties.getProperty(GATEWAY_PROTOCOL_ID));

		try {
			printSeparator();

			Gateway gateway = coreService.getOnlineGateway(gatewayType);

			printSeparator();

			Device device = getOrAddDevice(deviceId, gateway);

			printSeparator();

			Capability capability = getOrAddHumidityCapability();

			printSeparator();

			SensorType sensorType = getOrAddHumiditySensorType(capability);

			Sensor sensor = getOrAddSensor(sensorId, device, sensorType);

			printSeparator();

			Authentication authentication = coreService.getAuthentication(device);

			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);
			gatewayCloud = GatewayProtocol.REST.equals(gatewayType)
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

	private Capability getOrAddHumidityCapability()
	throws IOException {
		Capability[] capabilities = coreService.getCapabilities();
		for (Capability nextCapability : capabilities) {
			if (EntityFactory.ROOM_HUMIDITY.equals(nextCapability.getName())) {
				return nextCapability;
			}
		}

		printWarning(String.format("No '%1$s' Capability found", EntityFactory.ROOM_HUMIDITY));

		printSeparator();

		Capability templateCapability = EntityFactory.buildHumidityCapability();
		Capability capability = coreService.addCapability(templateCapability);

		printNewLine();
		printProperty(CAPABILITY_ID, capability.getId());

		return capability;
	}

	private SensorType getOrAddHumiditySensorType(Capability capability)
	throws IOException {
		SensorType[] sensorTypes = coreService.getSensorTypes();
		for (SensorType nextSensorType : sensorTypes) {
			if (EntityFactory.HUMIDITY_SENSORS.equals(nextSensorType.getName())) {
				return nextSensorType;
			}
		}

		printWarning(String.format("No '%1$s' Sensor Type found", EntityFactory.HUMIDITY_SENSORS));

		printSeparator();

		SensorTypeCapability sensorTypeCapability = new SensorTypeCapability();
		sensorTypeCapability.setId(capability.getId());
		sensorTypeCapability.setType(CapabilityType.MEASURE);

		SensorType templateSensorType = EntityFactory.buildHumiditySensorType(sensorTypeCapability);
		SensorType sensorType = coreService.addSensorType(templateSensorType);

		printNewLine();
		printProperty(SENSOR_TYPE_ID, capability.getId());

		return sensorType;
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
				Measure measure = EntityFactory.buildHumidityMeasure(sensor, capability);

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