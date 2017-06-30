package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import commons.api.CoreService;
import commons.api.GatewayCloud;
import commons.api.GatewayCloudHttp;
import commons.api.GatewayCloudMqtt;
import commons.model.Authentication;
import commons.model.Device;
import commons.model.Gateway;
import commons.model.GatewayMeasure;
import commons.model.GatewayType;
import commons.utils.Console;
import commons.utils.Constants;
import commons.utils.FileUtil;
import commons.utils.ObjectFactory;
import commons.utils.SecurityUtil;

/**
 * Main entry point of the sample
 */
public class Main {

	private static final String TITLE = "SAP Internet of Things for the Cloud Foundry Environment -- Gateway Cloud Client";
	private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";

	private static Properties properties;

	static {
		System.out.println();
		System.out.println(TITLE);
		System.out.println();

		init();
	}

	public static void main(String[] args) {
		String host = properties.getProperty(Constants.IOT_HOST);
		String user = properties.getProperty(Constants.IOT_USER);
		String password = properties.getProperty(Constants.IOT_PASSWORD);
		String deviceId = properties.getProperty(Constants.DEVICE_ID);
		GatewayType gatewayType = GatewayType
			.fromValue(properties.getProperty(Constants.GATEWAY_TYPE));

		CoreService coreService = new CoreService(host, user, password);

		try {
			System.out.println(Constants.SEPARATOR);
			Gateway gateway = coreService.getOnlineGateway(gatewayType);

			System.out.println(Constants.SEPARATOR);
			Device device = coreService.getOrAddDevice(deviceId, gateway);

			System.out.println(Constants.SEPARATOR);
			Authentication authentication = coreService.getAuthentication(device);
			SSLSocketFactory sslSocketFactory = SecurityUtil.getSSLSocketFactory(device,
				authentication);

			System.out.println(Constants.SEPARATOR);
			GatewayCloud gatewayCloud = GatewayType.REST.equals(gatewayType)
				? new GatewayCloudHttp(device, sslSocketFactory)
				: new GatewayCloudMqtt(device, sslSocketFactory);

			gatewayCloud.connect(host);

			sendTemperature(gatewayCloud);
		}
		catch (IOException | GeneralSecurityException | IllegalStateException e) {
			System.err.println(String.format("[ERROR] Execution failure: %1$s", e.getMessage()));
			System.exit(1);
		}
	}

	/**
	 * Sends random temperature measures on behalf of the device to the Gateway Cloud. Temperature
	 * measure is being sent each second during the 5 seconds period.
	 */
	private static void sendTemperature(final GatewayCloud gatewayCloud)
	throws IOException {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					gatewayCloud.send(ObjectFactory.buildTemperatureMeasure(),
						GatewayMeasure.class);
				}
				catch (IOException e) {
					// do nothing
				}
				finally {
					System.out.println(Constants.SEPARATOR);
				}
			}

		}, 0l, 1000, TimeUnit.MILLISECONDS);

		try {
			executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			throw new IOException("Interrupted exception", e);
		}
		finally {
			gatewayCloud.disconnect();
			executor.shutdownNow();
		}
	}

	/**
	 * Reads the configuration properties from file located in the same directory to JAR. Creates an
	 * empty properties collection if file does not exist.
	 */
	private static void init() {
		File jar = new File(
			Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		File config = new File(
			jar.getParentFile().getAbsolutePath().concat("/").concat(CONFIG_PROPERTIES_FILE_NAME));

		try {
			if (config.exists()) {
				properties = FileUtil.readProperties(new FileInputStream(config));
			}
			else {
				properties = new Properties();
			}
		}
		catch (IOException e) {
			properties = new Properties();
		}
		finally {
			prompt();
			print();
		}
	}

	/**
	 * Prompts for the missing configuration properties
	 */
	private static void prompt() {
		Console console = Console.getInstance();

		String host = properties.getProperty(Constants.IOT_HOST);
		host = console.awaitNextLine(host, "Hostname (i.e 'test.cp.iot.sap'): ");
		properties.setProperty(Constants.IOT_HOST, host);

		String user = properties.getProperty(Constants.IOT_USER);
		user = console.awaitNextLine(user, "Username (i.e. 'root#0'): ");
		properties.setProperty(Constants.IOT_USER, user);

		String gatewayType = properties.getProperty(Constants.GATEWAY_TYPE);
		gatewayType = console.awaitNextLine(gatewayType, "Gateway Type ('rest' or 'mqtt'): ");
		properties.setProperty(Constants.GATEWAY_TYPE,
			GatewayType.fromValue(gatewayType).getValue());

		String physicalAddress = properties.getProperty(Constants.DEVICE_ID);
		physicalAddress = console.awaitNextLine(physicalAddress, "Device ID (i.e '100'): ");
		properties.setProperty(Constants.DEVICE_ID, physicalAddress);

		String password = properties.getProperty(Constants.IOT_PASSWORD);
		password = console.nextPassword("Password for your user: ");
		properties.setProperty(Constants.IOT_PASSWORD, password);

		console.close();
	}

	/**
	 * Prints out the resulting configuration properties to the console. Skips user password and
	 * properties having empty values.
	 */
	private static void print() {
		System.out.println();
		System.out.println("Properties:");
		for (Object key : properties.keySet()) {
			if (Constants.IOT_PASSWORD.equals(key) ||
				properties.get(key).toString().trim().isEmpty()) {
				continue;
			}
			System.out.printf("\t%-15s : %s %n", key, properties.get(key));
		}
		System.out.println();
	}

}