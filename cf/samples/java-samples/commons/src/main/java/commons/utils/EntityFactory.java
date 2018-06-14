package commons.utils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import commons.model.Capability;
import commons.model.CapabilityType;
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

	private static final String SAMPLE_SENSOR_NAME = "SampleSensor";
	private static final String SAMPLE_DEVICE_NAME = "SampleDevice";

	private static final String CONTROL_UNIT_SENSOR_TYPE_NAME = "ControlUnit";

	private static final String AMBIENT_CAPABILITY_ALTERNATE_ID = "ambient";
	private static final String SWITCH_CAPABILITY_ALTERNATE_ID = "switch";

	private static final String AMBIENT_CAPABILITY_NAME = "Ambient";
	private static final String SWITCH_CAPABILITY_NAME = "Switch";

	private static final String HUMIDITY_PROPERTY_NAME = "Humidity";
	private static final String TEMPERATURE_PROPERTY_NAME = "Temperature";
	private static final String LIGHT_PROPERTY_NAME = "Light";
	private static final String TEXT_PROPERTY_NAME = "Text";
	private static final String LED_PROPERTY_NAME = "LED";

	private static final String HUMIDITY_PROPERTY_UOM = "%";
	private static final String TEMPERATURE_PROPERTY_UOM = "°C";
	private static final String LIGHT_PROPERTY_UOM = "Lux";

	public static Measure buildAmbientMeasure(Sensor sensor, Capability capability) {
		Measure measure = new Measure();

		measure.setCapabilityAlternateId(capability.getAlternateId());
		measure.setSensorAlternateId(sensor.getAlternateId());
		measure.setMeasures(
			new Object[][] { { buildHumidityPercentage(), buildDegreesCelsius(), buildLightIlluminance() } });

		return measure;
	}

	public static Command buildSwitchCommand(Sensor sensor, Capability capability) {
		Command command = new Command();

		command.setCapabilityId(capability.getId());
		command.setSensorId(sensor.getId());

		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put(TEXT_PROPERTY_NAME, buildTextValue());
		properties.put(LED_PROPERTY_NAME, buildLEDValue());

		command.setProperties(properties);

		return command;
	}

	public static Sensor buildSampleSensor(Device device, SensorType sensorType) {
		Sensor sensor = new Sensor();

		sensor.setDeviceId(device.getId());
		sensor.setSensorTypeId(sensorType.getId());
		sensor.setName(SAMPLE_SENSOR_NAME);

		return sensor;
	}

	public static Device buildSampleDevice(Gateway gateway) {
		Device device = new Device();

		device.setGatewayId(gateway.getId());
		device.setName(SAMPLE_DEVICE_NAME);

		return device;
	}

	public static SensorType buildSampleSensorType(Capability measureCapability, Capability commandCapability) {
		SensorType sensorType = new SensorType();

		sensorType.setName(CONTROL_UNIT_SENSOR_TYPE_NAME);

		SensorTypeCapability measure = new SensorTypeCapability();
		measure.setId(measureCapability.getId());
		measure.setType(CapabilityType.MEASURE);

		SensorTypeCapability command = new SensorTypeCapability();
		command.setId(commandCapability.getId());
		command.setType(CapabilityType.COMMAND);

		sensorType.setCapabilities(new SensorTypeCapability[] { measure, command });

		return sensorType;
	}

	public static Capability buildAmbientCapability() {
		Capability capability = new Capability();

		capability.setAlternateId(AMBIENT_CAPABILITY_ALTERNATE_ID);
		capability.setName(AMBIENT_CAPABILITY_NAME);
		capability.setProperties(
			new Property[] { buildHumidityProperty(), buildTemperatureProperty(), buildLightProperty() });

		return capability;
	}

	public static Capability buildSwitchCapability() {
		Capability capability = new Capability();

		capability.setAlternateId(SWITCH_CAPABILITY_ALTERNATE_ID);
		capability.setName(SWITCH_CAPABILITY_NAME);
		capability.setProperties(new Property[] { buildTextProperty(), buildLEDProperty() });

		return capability;
	}

	private static Property buildHumidityProperty() {
		Property property = new Property();

		property.setName(HUMIDITY_PROPERTY_NAME);
		property.setDataType(PropertyType.INTEGER);
		property.setUnitOfMeasure(HUMIDITY_PROPERTY_UOM);

		return property;
	}

	private static Property buildTemperatureProperty() {
		Property property = new Property();

		property.setName(TEMPERATURE_PROPERTY_NAME);
		property.setDataType(PropertyType.FLOAT);
		property.setUnitOfMeasure(TEMPERATURE_PROPERTY_UOM);

		return property;
	}

	private static Property buildLightProperty() {
		Property property = new Property();

		property.setName(LIGHT_PROPERTY_NAME);
		property.setDataType(PropertyType.INTEGER);
		property.setUnitOfMeasure(LIGHT_PROPERTY_UOM);

		return property;
	}

	private static Property buildTextProperty() {
		Property property = new Property();

		property.setName(TEXT_PROPERTY_NAME);
		property.setDataType(PropertyType.STRING);

		return property;
	}

	private static Property buildLEDProperty() {
		Property property = new Property();

		property.setName(LED_PROPERTY_NAME);
		property.setDataType(PropertyType.BOOLEAN);

		return property;
	}

	private static int buildHumidityPercentage() {
		int min = 0;
		int max = 100;

		return new Random().nextInt(max - min + 1) + min;
	}

	private static float buildDegreesCelsius() {
		float min = -100.0f;
		float max = 100.0f;

		float randomFloat = new Random().nextFloat() * (max - min) + min;

		return BigDecimal.valueOf(randomFloat).setScale(1, BigDecimal.ROUND_HALF_EVEN).floatValue();
	}

	private static int buildLightIlluminance() {
		int min = 0;
		int max = 1000;

		return new Random().nextInt(max - min + 1) + min;
	}

	private static boolean buildLEDValue() {
		return new Random().nextBoolean();
	}

	private static String buildTextValue() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
