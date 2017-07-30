package commons;

import java.io.IOException;

import commons.api.CoreService;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Sensor;
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

	protected Sensor getOrAddDeviceSensor(String sensorId, Device device)
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

}
