package commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import commons.model.Command;
import commons.model.Device;
import commons.model.gateway.Measure;

public class ObjectFactory {

	public static Device buildDevice() {
		Device device = new Device();
		device.setName("device-".concat(buildString()).concat("-name"));
		return device;
	}

	public static Measure buildTemperatureMeasure() {
		Measure measure = new Measure();
		// ID '1' stands for Temperature
		measure.setMeasureIds(new int[] { 1 });
		measure.setValues(new String[] { String.valueOf(buildDegreesCelsius()) });
		return measure;
	}

	public static Command buildToggleValveCommand() {
		Command command = new Command();
		// ID '3' stands for ToggleValve
		command.setCapabilityId("3");
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("val", new Random().nextBoolean() ? "1" : "0");
		command.setCommand(arguments);
		return command;
	}

	private static int buildDegreesCelsius() {
		int min = -100;
		int max = 100;
		return new Random().nextInt((max - min) + 1) + min;
	}

	private static String buildString() {
		return UUID.randomUUID().toString();
	}

}
