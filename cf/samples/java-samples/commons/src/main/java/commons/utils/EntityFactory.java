package commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.gateway.Measure;

public class EntityFactory {

	public static Device buildDevice(Gateway gateway) {
		Device device = new Device();

		device.setGatewayId(gateway.getId());
		device.setName("device-".concat(buildString()).concat("-name"));

		return device;
	}

	public static Sensor buildSensor(Device device) {
		/*
		 * ID "0" stands for default Sensor Type
		 */
		SensorType sensorType = new SensorType();
		sensorType.setId("0");

		return buildSensor(device, sensorType);
	}

	public static Sensor buildSensor(Device device, SensorType sensorType) {
		Sensor sensor = new Sensor();

		sensor.setDeviceId(device.getId());
		sensor.setSensorTypeId(sensorType.getId());
		sensor.setName("sensor-".concat(buildString()).concat("-name"));

		return sensor;
	}

	public static Measure buildTemperatureMeasure(Sensor sensor) {
		Measure measure = new Measure();

		/*
		 * Physical address '1' stands for Temperature measure in the default Sensor Type
		 */
		measure.setMeasureIds(new String[] { "1" });
		measure.setValues(new String[] { String.format("%.1f", buildDegreesCelsius()) });
		measure.setLogNodeAddr(sensor.getPhysicalAddress());

		return measure;
	}

	public static Command buildToggleValveCommand(Sensor sensor) {
		Command command = new Command();

		/*
		 * ID '*_0_0_3' stands for Toggle Valve command in the default Sensor Type
		 */
		command.setCapabilityId("*_0_0_3");
		Map<String, Object> properties = new HashMap<>();
		properties.put("val", new Random().nextBoolean() ? "1" : "0");
		command.setProperties(properties);
		command.setSensorId(sensor.getId());

		return command;
	}

	private static float buildDegreesCelsius() {
		float min = -100.0f;
		float max = 100.0f;

		return new Random().nextFloat() * (max - min) + min;
	}

	private static String buildString() {
		return UUID.randomUUID().toString();
	}

}
