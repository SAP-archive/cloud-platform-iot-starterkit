import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Insert {
	
	public static void main(String[] args)
	throws IOException {
		// set your proxy
		// System.setProperty("https.proxyHost", "proxy");
		// System.setProperty("https.proxyPort", "8080");

		// set your variables here
		String accountName = "your_trial_account_name";
		String deviceId = "your_device_id";
		String deviceToken = "your_oauth_token";
		String messageTypeId = "your_message_type_id";

		insert(accountName, deviceId, deviceToken, messageTypeId);
	}

	/**
	 * Sends messages to MMS
	 */
	public static void insert(String accountName, String deviceId, String deviceToken,
		String messageTypeId)
	throws IOException {
		URL url = new URL("https://iotmms" + accountName +
			".hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/" + deviceId);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		connection.setRequestProperty("Authorization", "Bearer " + deviceToken);

		// send message of message type and the corresponding payload layout that you
		// defined in the IoT Services Cockpit
		String body = "{\"mode\":\"async\", \"messageType\":\"" + messageTypeId +
			"\", \"messages\":[{\"sensor\":\"sensor1\", \"value\":\"20\", \"timestamp\":1468991773}]}";
		byte[] outputInBytes = body.getBytes("UTF-8");

		// It is also possible to send multiple messages (3 in this example) in a single request
		// that conform to the same message type.
		// String body = "{\"mode\":\"async\", " + "\"messageType\":\"" + messageTypeId +
		// "\",\"messages\":[{\"sensor\":\"sensor1\",\"value\":\"20\"," +
		// "\"timestamp\":1468991773},{\"sensor\":\"sensor1\"," +
		// "\"value\":\"21\",\"timestamp\":1468991873},{\"sensor\":\"sensor1\"," +
		// "\"value\":\"22\",\"timestamp\":1468991973}]}";

		// Because every message field in a message type definition defines its position (see
		// message type example above) it is also possible to compress the messages array by
		// omitting the field names.
		// Please be aware is that value order is very important in this case (it should match to
		// the message type field positions like specified during message type creation)
		// String body = "{\"mode\":\"async\",\"messageType\":\"" + messageTypeId +
		// "\",\"messages\":[[\"sensor1\",\"20\",1468991773],[\"sensor1\",\"21\",1468991873],
		// [\"sensor1\",\"22\",1468991973]]}";

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// send request
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(outputInBytes);
		outputStream.close();

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
