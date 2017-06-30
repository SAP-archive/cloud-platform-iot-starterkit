package commons.utils;

import java.util.Random;
import java.util.UUID;

import commons.model.Device;
import commons.model.GatewayMeasure;

public class ObjectFactory {

	public static Device buildDevice() {
		Device device = new Device();
		device.setName("device-".concat(buildString()).concat("-name"));
		return device;
	}

	public static GatewayMeasure buildTemperatureMeasure() {
		GatewayMeasure measure = new GatewayMeasure();
		// ID '1' stands for Temperature
		measure.setMeasureIds(new int[] { 1 });
		measure.setValues(new String[] { String.valueOf(buildDegreesCelsius()) });
		return measure;
	}

	public static int buildDegreesCelsius() {
		int min = -100;
		int max = 100;
		return new Random().nextInt((max - min) + 1) + min;
	}

	public static String buildString() {
		return UUID.randomUUID().toString();
	}

}
