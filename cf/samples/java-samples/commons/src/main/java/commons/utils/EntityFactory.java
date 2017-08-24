package commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import commons.model.Capability;
import commons.model.Command;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.Property;
import commons.model.PropertyType;
import commons.model.Sensor;
import commons.model.SensorType;
import commons.model.SensorTypeCapability;
import commons.model.gateway.Measure;

public class EntityFactory {

	public static final String HUMIDITY = "Humidity";

	public static final String ROOM_HUMIDITY = "Room Humidity";

	public static final String HUMIDITY_SENSORS = "Humidity Sensors";

	public static final String TEXT = "Text";

	public static final String DISPLAY_TEXT = "Display Text";

	public static final String DISPLAY_SENSORS = "Display Sensors";

	public static Device buildDevice(Gateway gateway) {
		Device device = new Device();

		device.setGatewayId(gateway.getId());
		device.setName("device-".concat(buildString()));

		return device;
	}

	public static Sensor buildSensor(Device device, SensorType sensorType) {
		Sensor sensor = new Sensor();

		sensor.setDeviceId(device.getId());
		sensor.setSensorTypeId(sensorType.getId());
		sensor.setName("sensor-".concat(buildString()));

		return sensor;
	}

	public static SensorType buildHumiditySensorType(SensorTypeCapability sensorTypeCapability) {
		SensorType sensorType = new SensorType();

		sensorType.setName(HUMIDITY_SENSORS);
		sensorType.setCapabilities(new SensorTypeCapability[] { sensorTypeCapability });

		return sensorType;
	}

	public static SensorType buildDisplayTextSensorType(SensorTypeCapability sensorTypeCapability) {
		SensorType sensorType = new SensorType();

		sensorType.setName(DISPLAY_SENSORS);
		sensorType.setCapabilities(new SensorTypeCapability[] { sensorTypeCapability });

		return sensorType;
	}

	public static Measure buildTemperatureMeasure(Sensor sensor, Capability capability) {
		Measure measure = new Measure();

		measure.setMeasureIds(new String[] { capability.getAlternateId() });
		measure.setValues(new String[] { String.format("%.1f", buildDegreesCelsius()) });
		measure.setLogNodeAddr(sensor.getAlternateId());

		return measure;
	}

	public static Measure buildHumidityMeasure(Sensor sensor, Capability capability) {
		Measure measure = new Measure();

		measure.setMeasureIds(new String[] { capability.getAlternateId() });
		measure.setValues(new String[] { String.valueOf(buildHumidityPercentage()) });
		measure.setLogNodeAddr(sensor.getAlternateId());

		return measure;
	}

	public static Capability buildHumidityCapability() {
		Capability capability = new Capability();

		Property property = new Property();
		property.setName(HUMIDITY);
		property.setDataType(PropertyType.INTEGER);
		property.setUnitOfMeasure("%");

		capability.setName(ROOM_HUMIDITY);
		capability.setProperties(new Property[] { property });

		return capability;
	}

	public static Capability buildDisplayTextCapability() {
		Capability capability = new Capability();

		Property property = new Property();
		property.setName(TEXT);
		property.setDataType(PropertyType.STRING);

		capability.setName(DISPLAY_TEXT);
		capability.setProperties(new Property[] { property });

		return capability;
	}

	public static Command buildToggleValveCommand(Sensor sensor, Capability capability) {
		Command command = new Command();

		command.setCapabilityId(capability.getId());
		Map<String, Object> properties = new HashMap<>();
		properties.put("val", new Random().nextBoolean() ? "1" : "0");
		command.setProperties(properties);
		command.setSensorId(sensor.getId());

		return command;
	}

	public static Command buildDisaplyTextCommand(Sensor sensor, Capability capability) {
		Command command = new Command();

		command.setCapabilityId(capability.getId());
		Map<String, Object> properties = new HashMap<>();
		properties.put(TEXT, "Hello IoT");
		command.setProperties(properties);
		command.setSensorId(sensor.getId());

		return command;
	}

	private static float buildDegreesCelsius() {
		float min = -100.0f;
		float max = 100.0f;

		return new Random().nextFloat() * (max - min) + min;
	}

	private static int buildHumidityPercentage() {
		int min = 0;
		int max = 100;

		return new Random().nextInt(max - min + 1) + min;
	}

	private static String buildString() {
		return UUID.randomUUID().toString();
	}

}
