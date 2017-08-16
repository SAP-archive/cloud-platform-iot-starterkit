package commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import commons.model.GatewayType;
import commons.utils.Console;
import commons.utils.FileUtil;

/**
 * An abstraction over all sample applications.
 */
public abstract class AbstractSample {

	private static final String PRODUCT_TITLE = "SAP Internet of Things for the Cloud Foundry Environment";

	private static final String CONFIGURATIONS_FILE_NAME = "sample.properties";

	public static final String IOT_HOST = "iot.host";
	public static final String IOT_USER = "iot.user";
	public static final String IOT_PASSWORD = "iot.password";
	public static final String DEVICE_ID = "device.id";
	public static final String SENSOR_ID = "sensor.id";
	public static final String SENSOR_TYPE_ID = "sensor.type.id";
	public static final String CAPABILITY_ID = "capability.id";
	public static final String GATEWAY_TYPE = "gateway.type";
	public static final String PROXY_PORT = "proxy.port";
	public static final String PROXY_HOST = "proxy.host";

	protected Properties properties;

	public AbstractSample() {
		printNewLine();
		System.out.println(PRODUCT_TITLE);
		System.out.println(getDescription());
		printNewLine();

		init();
	}

	/**
	 * Reads the configuration properties from the file located in the same directory to JAR
	 * archive. Sticks to the empty properties collection if the configuration file does not exist.
	 */
	protected void init() {
		File jar = new File(
			AbstractSample.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		File config = new File(
			jar.getParentFile().getAbsolutePath().concat("/").concat(CONFIGURATIONS_FILE_NAME));

		properties = new Properties();

		try {
			if (config.exists()) {
				properties = FileUtil.readProperties(new FileInputStream(config));
			}
		}
		catch (IOException e) {
			// do nothing
		}
		finally {
			promptProperties();
			printProperties();
		}
	}

	/**
	 * Prints out the resulting configuration properties to the console. Skips user password and
	 * properties having empty values.
	 */
	protected void printProperties() {
		printNewLine();
		System.out.println("Properties:");
		for (Object key : properties.keySet()) {
			if (IOT_PASSWORD.equals(key) || properties.get(key).toString().trim().isEmpty()) {
				continue;
			}
			printProperty(key, properties.get(key));
		}
		printNewLine();
	}

	protected void printSeparator() {
		for (int i = 0; i < 80; i++) {
			System.out.print("-");
		}
		printNewLine();
	}

	protected void printError(String message) {
		System.out.println(String.format("[ERROR] %1$s", message));
	}

	protected void printWarning(String message) {
		printNewLine();
		System.out.println(String.format("[WARN] %1$s", message));
	}

	protected void printProperty(Object key, Object value) {
		System.out.printf("\t%-15s : %s %n", key, value);
	}

	protected void printNewLine() {
		System.out.println();
	}

	/**
	 * Prompts the user for missing configuration properties.
	 */
	protected void promptProperties() {
		Console console = Console.getInstance();

		String host = properties.getProperty(IOT_HOST);
		host = console.awaitNextLine(host, "Hostname (e.g. 'test.cp.iot.sap'): ");
		properties.setProperty(IOT_HOST, host);

		String user = properties.getProperty(IOT_USER);
		user = console.awaitNextLine(user, "Username (e.g. 'root'): ");
		properties.setProperty(IOT_USER, user);

		String gatewayType = properties.getProperty(GATEWAY_TYPE);
		gatewayType = console.awaitNextLine(gatewayType, "Gateway Type ('rest' or 'mqtt'): ");
		properties.setProperty(GATEWAY_TYPE, GatewayType.fromValue(gatewayType).getValue());

		String deviceId = properties.getProperty(DEVICE_ID);
		deviceId = console.awaitNextLine(deviceId, "Device ID (e.g. '100'): ");
		properties.setProperty(DEVICE_ID, deviceId);

		String sensorId = properties.getProperty(SENSOR_ID);
		sensorId = console.awaitNextLine(sensorId, "Sensor ID (e.g. '100'): ");
		properties.setProperty(SENSOR_ID, sensorId);

		String password = properties.getProperty(IOT_PASSWORD);
		password = console.nextPassword("Password for your user: ");
		properties.setProperty(IOT_PASSWORD, password);

		console.close();
	};

	/**
	 * Gets a description of the sample application.
	 */
	protected abstract String getDescription();

	/**
	 * Runs the logic of the sample application.
	 */
	protected abstract void run()
	throws SampleException;

}
