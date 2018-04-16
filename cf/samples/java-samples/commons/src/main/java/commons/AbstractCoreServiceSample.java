package commons;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import commons.api.CoreService;
import commons.model.Capability;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.SensorTypeCapability;
import commons.utils.Console;
import commons.utils.EntityFactory;

public abstract class AbstractCoreServiceSample
extends AbstractSample {

	protected CoreService coreService;

	private Comparator<SensorTypeCapability> sensorTypeCapabilityComparator;

	public AbstractCoreServiceSample() {
		super();

		String host = properties.getProperty(IOT_HOST);
		String user = properties.getProperty(IOT_USER);
		String password = properties.getProperty(IOT_PASSWORD);

		coreService = new CoreService(host, user, password);

		sensorTypeCapabilityComparator = Comparator.comparing(SensorTypeCapability::getId);
	}

	protected Device getOrAddDevice(String deviceId, Gateway gateway)
	throws IOException {
		Device device;
		try {
			device = coreService.getDevice(deviceId, gateway);
		} catch (IOException | IllegalStateException e) {
			Console.printWarning(e.getMessage());

			Console.printSeparator();

			Device deviceTemplate = EntityFactory.buildSampleDevice(gateway);
			device = coreService.addDevice(deviceTemplate);

			Console.printNewLine();
			Console.printProperty(DEVICE_ID, device.getId());
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
			} else {
				Console.printWarning(String.format("A Sensor '%1$s' has no reference to Sensor Type '%2$s'", sensorId,
					sensorType.getId()));
			}
		} else {
			Console.printWarning(
				String.format("No Sensor '%1$s' is attached to the Device '%2$s'", sensorId, device.getId()));
		}

		Console.printSeparator();

		Sensor sensorTemplate = EntityFactory.buildSampleSensor(device, sensorType);
		sensor = coreService.addSensor(sensorTemplate);

		Console.printNewLine();
		Console.printProperty(SENSOR_ID, sensor.getId());

		return sensor;
	}

	protected SensorType getOrAddSensorType(Capability measureCapability, Capability commandCapability)
	throws IOException {
		SensorType sensorTypeTemplate = EntityFactory.buildSampleSensorType(measureCapability, commandCapability);

		Arrays.asList(sensorTypeTemplate.getCapabilities()).sort(sensorTypeCapabilityComparator);

		SensorType[] existingSensorTypes = coreService.getSensorTypes();

		List<SensorType> filteredSensorTypes = Arrays.stream(existingSensorTypes).filter(st -> {
			Arrays.asList(st.getCapabilities()).sort(sensorTypeCapabilityComparator);
			return st.equals(sensorTypeTemplate);
		}).distinct().collect(Collectors.toList());

		if (filteredSensorTypes.size() == 1) {
			return filteredSensorTypes.get(0);
		}

		Console.printWarning(String.format("No '%1$s' Sensor Type found", sensorTypeTemplate.getName()));

		Console.printSeparator();

		SensorType sensorType = coreService.addSensorType(sensorTypeTemplate);

		Console.printNewLine();
		Console.printProperty(SENSOR_TYPE_ID, sensorType.getId());

		return sensorType;
	}

	protected Capability getOrAddCapability(Capability capabilityTemplate)
	throws IOException {
		Capability[] existingCapabilities = coreService.getCapabilities();

		List<Capability> filteredCapabilities = Arrays.stream(existingCapabilities).distinct()
			.filter(c -> c.equals(capabilityTemplate)).collect(Collectors.toList());

		if (filteredCapabilities.size() == 1) {
			return filteredCapabilities.get(0);
		}

		Console.printWarning(String.format("No '%1$s' Capability found", capabilityTemplate.getName()));

		Console.printSeparator();

		Capability capability = coreService.addCapability(capabilityTemplate);

		Console.printNewLine();
		Console.printProperty(CAPABILITY_ID, capability.getId());

		return capability;
	}

}
