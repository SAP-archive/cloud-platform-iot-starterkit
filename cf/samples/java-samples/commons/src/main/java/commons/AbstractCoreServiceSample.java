package commons;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import commons.api.CoreService;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.utils.EntityFactory;

public abstract class AbstractCoreServiceSample
extends AbstractSample {

	protected CoreService coreService;

	public AbstractCoreServiceSample() {
		super();

		String host = properties.getProperty(IOT_HOST);
		String user = properties.getProperty(IOT_USER);
		String password = properties.getProperty(IOT_PASSWORD);

		coreService = new CoreService(host, user, password);
	}

	protected Device getOrAddDevice(String deviceId, Gateway gateway)
	throws IOException {
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

	protected Sensor getOrAddSensor(String sensorId, Device device, SensorType sensorType)
	throws IOException {
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
		if (sensor != null) {
			if (sensor.getSensorTypeId().equals(sensorType.getId())) {
				return sensor;
			}
			else {
				printWarning(String.format("A sensor '%1$s' has no reference to Sensor Type '%2$s'",
					sensorId, sensorType.getId()));
			}
		}
		else {
			printWarning(String.format("No sensor '%1$s' is attached to the device '%2$s'",
				sensorId, device.getId()));
		}

		printSeparator();

		Sensor sensorTemplate = EntityFactory.buildSensor(device, sensorType);
		sensor = coreService.addSensor(sensorTemplate);

		printNewLine();
		printProperty(SENSOR_ID, sensor.getId());

		return sensor;
	}

	protected void receiveMeasures(final Device device, final Capability capability)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					coreService.getLatestMeasures(device, capability);
				}
				catch (IOException e) {
					printError(e.getMessage());
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
