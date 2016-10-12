// file transfer example
// transmits file test.txt

// message type layout that is used
//         timestamp: date
//         filename:  string
//         data:      binary

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Base64;

public class FileTransfer {
	
	public static void main(String[] args)
	throws IOException {
		String my_filename = "test.txt";
		Path fileToSend = FileSystems.getDefault().getPath(my_filename);
                byte[] base64EncodedFile = Base64.getEncoder().encode(Files.readAllBytes(fileToSend));
                String my_data = new String(base64EncodedFile);
		// System.out.println(my_data);

		int timestamp_int =  (int) (System.currentTimeMillis() / 1000L);
		String timestamp_string = Integer.toString(timestamp_int);
		// System.out.println(timestamp_string);

		// set your proxy
		// System.setProperty("https.proxyHost", "proxy");
		// System.setProperty("https.proxyPort", "8080");

		// ==== adapt to your configuration here ====
		String accountName = "p<digits>trial";
		String deviceId = "<your_device_id>";
		String deviceToken = "<your_oauth_token>";
		String messageTypeId = "<your_message_type_id>";
		// ==========================================

		String url_string = "https://iotmms" + accountName + ".hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/" + deviceId;
		String payload = "{\"mode\":\"async\", \"messageType\":\"" + messageTypeId +
			"\", \"messages\":[{\"timestamp\":" + timestamp_string + ", \"filename\":\"" + my_filename + "\", \"data\":\"" + my_data + "\"}]}";
		String response=post_message(url_string, deviceToken, payload);

		System.out.println(response);
	}

	public static String post_message(String url_string, String credential, String payload)
	throws IOException {
		URL url = new URL(url_string);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		connection.setRequestProperty("Authorization", "Bearer " + credential);

		byte[] outputInBytes = payload.getBytes("UTF-8");

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
		return(response.toString());

	}
}
