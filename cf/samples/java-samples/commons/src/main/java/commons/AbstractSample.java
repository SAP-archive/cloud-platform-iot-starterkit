package commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import commons.utils.Constants;
import commons.utils.FileUtil;

/**
 * An abstraction over all sample applications.
 */
public abstract class AbstractSample {

	private static final String PRODUCT_TITLE = "SAP Internet of Things for the Cloud Foundry Environment";

	private static final String CONFIGURATIONS_FILE_NAME = "config.properties";

	protected Properties properties;

	public AbstractSample() {
		System.out.println();
		System.out.println(PRODUCT_TITLE);
		System.out.println(getDescription());
		System.out.println();

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

	/**
	 * Gets a description of the sample application.
	 */
	protected abstract String getDescription();

	/**
	 * Prompts the user for missing configuration properties.
	 */
	protected abstract void promptProperties();

	/**
	 * Executes the logic of the sample application.
	 */
	protected abstract void execute();

}
