import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Retrieve {
	
	public static void main(String[] args)
	throws IOException {
		// set your proxy
		// System.setProperty("https.proxyHost", "proxy");
		// System.setProperty("https.proxyPort", "8080");

		// set your variables here
		String accountName = "your_trial_account_name";
		String deviceId = "your_device_id";
		String deviceToken = "your_oauth_token";

		retrieve(accountName, deviceId, deviceToken);
	}

	/**
	 * Retrieves messages from the PUSH API
	 */
	public static void retrieve(String accountName, String deviceId, String deviceToken)
	throws IOException {
		URL url = new URL("https://iotmms" + accountName +
			".hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/" + deviceId);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Bearer " + deviceToken);

		// get response
		InputStream inputStream = null;
		if (connection.getResponseCode() >= 400) {
			inputStream = connection.getErrorStream();
		}
		else {
			inputStream = connection.getInputStream();
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = bufferedReader.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		bufferedReader.close();

		System.out.println(response.toString());
	}

}
