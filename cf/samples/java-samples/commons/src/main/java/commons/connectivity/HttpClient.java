package commons.connectivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.JsonSyntaxException;

import commons.utils.Constants;
import commons.utils.FileUtil;

public class HttpClient
extends AbstractClient {

	private HttpURLConnection connection;

	private String user;

	private String password;

	private SSLSocketFactory sslSocketFactory;

	private String destination;

	private HttpClient() {
		super();
	}

	public HttpClient(String user, String password) {
		this();

		this.user = user;
		this.password = password;
	}

	public HttpClient(SSLSocketFactory sslSocketFactory) {
		this();

		this.sslSocketFactory = sslSocketFactory;
	}

	public void connect(String destination)
	throws IOException {

		this.destination = destination;
		connection = openConnection(destination);

		if (user != null && password != null) {
			@SuppressWarnings("restriction")
			String base64 = new sun.misc.BASE64Encoder()
				.encode((user + ":" + password).getBytes(Constants.ENCODING));
			connection.setRequestProperty("Authorization", "Basic " + base64);
		}
		else if (sslSocketFactory != null && connection instanceof HttpsURLConnection) {
			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
		}
		else {
			throw new IOException("No authorization details provided");
		}
	}

	public void disconnect() {
		if (connection != null) {
			connection.disconnect();
			connection = null;
		}
	}

	public <T> void send(T payload, Class<T> clazz)
	throws IOException {
		doPostJson(payload, clazz);
	}

	public <T> T doGetJson(Class<T> clazz)
	throws IOException {
		try {
			return jsonParser.fromJson(doGetString(), clazz);
		}
		catch (JsonSyntaxException e) {
			throw new IOException("Unexpected JSON format returned", e);
		}
	}

	public String doGetString()
	throws IOException {

		if (connection == null) {
			connect(destination);
		}

		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/json");

		InputStream is = connect(connection);
		try {
			String response = readString(is);
			System.out.println(String.format("Response body %1$s", response));
			return response;
		}
		finally {
			FileUtil.closeStream(is);
			disconnect();
		}
	}

	public <T> T doPostJson(T payload, Class<T> clazz)
	throws IOException {
		String response = doPost(jsonParser.toJson(payload));
		try {
			return jsonParser.fromJson(response, clazz);
		}
		catch (JsonSyntaxException e) {
			throw new IOException("Unexpected JSON format returned", e);
		}
	}

	public <T> void doPost(T payload, Class<T> clazz)
	throws IOException {
		doPost(jsonParser.toJson(payload));
	}

	public String doPost(String request)
	throws IOException {

		if (connection == null) {
			connect(destination);
		}

		System.out.println(String.format("Request body %1$s", request));

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/json");

		byte[] bytes = request.getBytes(StandardCharsets.UTF_8);

		OutputStream os = connection.getOutputStream();
		try {
			os.write(bytes);
		}
		finally {
			FileUtil.closeStream(os);
		}

		InputStream is = connect(connection);
		try {
			String response = readString(is);
			System.out.println(String.format("Response body %1$s", response));
			return response;
		}
		finally {
			FileUtil.closeStream(is);
			disconnect();
		}
	}

	private HttpURLConnection openConnection(String destination)
	throws IOException {

		disconnect();

		System.out.println(String.format("Connect to %1$s", destination));
		System.out.println();

		URL url = null;
		try {
			url = new URL(destination);
		}
		catch (MalformedURLException e) {
			throw new IOException("Invalid HTTPS connection URL specified", e);
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpsURLConnection) url.openConnection();
		}
		catch (IOException e) {
			throw new IOException("Unable to open a HTTP connection", e);
		}

		return connection;
	}

	private InputStream connect(HttpURLConnection connection)
	throws IOException {

		connection.connect();

		int code = connection.getResponseCode();

		if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_MOVED_PERM) {
			InputStream errorStream = connection.getErrorStream();
			String errorResponse = null;
			try {
				if (errorStream == null) {
					errorResponse = connection.getResponseMessage();
				}
				else {
					errorResponse = readString(errorStream);
				}
			}
			finally {
				FileUtil.closeStream(errorStream);
			}
			throw new IOException(errorResponse);
		}

		return connection.getInputStream();
	}

	private String readString(InputStream stream)
	throws IOException {

		if (stream == null) {
			throw new IOException("The input stream was null");
		}

		StringBuilder sb = new StringBuilder();

		try {
			int next;
			while ((next = stream.read()) != -1) {
				sb.append((char) next);
			}
		}
		catch (IOException e) {
			throw new IOException("Unable to read from the input stream", e);
		}
		finally {
			FileUtil.closeStream(stream);
		}

		return sb.toString();
	}

}
