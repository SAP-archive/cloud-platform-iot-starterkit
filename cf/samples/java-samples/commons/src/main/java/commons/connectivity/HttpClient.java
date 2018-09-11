package commons.connectivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.JsonSyntaxException;

import commons.utils.Console;
import commons.utils.Constants;
import commons.utils.FileUtil;

public class HttpClient
extends AbstractClient {

	private HttpURLConnection connection;

	private String user;

	private String password;

	private SSLSocketFactory sslSocketFactory;

	private String serverUri;

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

	@Override
	public void connect(String serverUri)
	throws IOException {
		this.serverUri = serverUri;

		connection = openConnection(serverUri);

		if (user != null && password != null) {
			byte[] encodedBytes = Base64.getMimeEncoder()
				.encode((user + ":" + password).getBytes(Constants.DEFAULT_ENCODING));
			String base64 = new String(encodedBytes, Constants.DEFAULT_ENCODING);
			connection.setRequestProperty("Authorization", "Basic " + base64);
		} else if (sslSocketFactory != null && connection instanceof HttpsURLConnection) {
			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
		} else {
			throw new IOException("No authorization details provided");
		}
	}

	@Override
	public void disconnect() {
		if (connection != null) {
			connection.disconnect();
			connection = null;
		}
	}

	public String getServerUri() {
		return serverUri;
	}

	public <T> T doGet(Class<T> clazz)
	throws IOException {
		try {
			return jsonParser.fromJson(doGetAsString(), clazz);
		} catch (JsonSyntaxException e) {
			throw new IOException("Unexpected JSON format returned", e);
		}
	}

	public <T> T doPost(T payload, Class<T> clazz)
	throws IOException {
		return doPost(payload, clazz, clazz);
	}

	public <T, Y> Y doPost(T payload, Class<T> payloadClass, Class<Y> responseClass)
	throws IOException {
		try {
			String request = jsonParser.toJson(payload, payloadClass);
			return jsonParser.fromJson(doPostAsString(request), responseClass);
		} catch (JsonSyntaxException e) {
			throw new IOException("Unexpected JSON format returned", e);
		}
	}

	private String doGetAsString()
	throws IOException {

		if (connection == null) {
			connect(serverUri);
		}

		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/json");

		try {
			Response response = connect(connection);
			String body = response.getBody();

			Console.printText(String.format("Response [%1$d] %2$s", response.getCode(), body));

			return body;
		} finally {
			disconnect();
		}
	}

	private <T> String doPostAsString(String request)
	throws IOException {

		if (connection == null) {
			connect(serverUri);
		}

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/json");

		Console.printText(String.format("Request %1$s", request));
		Console.printNewLine();

		byte[] bytes = request.getBytes(Constants.DEFAULT_ENCODING);

		OutputStream os = connection.getOutputStream();
		try {
			os.write(bytes);
		} finally {
			FileUtil.closeStream(os);
		}

		try {
			Response response = connect(connection);
			String body = response.getBody();

			Console.printText(String.format("Response [%1$d] %2$s", response.getCode(), body));

			return body;
		} finally {
			disconnect();
		}
	}

	private HttpURLConnection openConnection(String destination)
	throws IOException {

		disconnect();

		Console.printText(String.format("Connect to %1$s", destination));
		Console.printNewLine();

		URI uri = null;
		try {
			URL url = new URL(destination);
			uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
				url.getQuery(), null);
		} catch (MalformedURLException | URISyntaxException e) {
			throw new IOException("Invalid HTTPS connection URL specified", e);
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpsURLConnection) uri.toURL().openConnection();
		} catch (IOException e) {
			throw new IOException("Unable to open a HTTP connection", e);
		}

		return connection;
	}

	private Response connect(HttpURLConnection connection)
	throws IOException {

		try {
			connection.connect();
		} catch (ConnectException e) {
			String errorMessage = "Unable to connect. Please check your Internet connection and proxy settings.";
			throw new IOException(errorMessage, e);
		}

		int code = connection.getResponseCode();

		InputStream stream;
		if (code < HttpURLConnection.HTTP_OK || code >= HttpURLConnection.HTTP_MULT_CHOICE) {
			stream = connection.getErrorStream();
		} else {
			stream = connection.getInputStream();
		}

		String body = null;
		try {
			if (stream == null) {
				body = connection.getResponseMessage();
			} else {
				body = readString(stream);
			}
		} finally {
			FileUtil.closeStream(stream);
		}

		return new Response(code, body);
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
		} catch (IOException e) {
			throw new IOException("Unable to read from the input stream", e);
		} finally {
			FileUtil.closeStream(stream);
		}

		return sb.toString();
	}

	private class Response {

		private int code;

		private String body;

		public Response(int code, String body) {
			this.code = code;
			this.body = body;
		}

		public int getCode() {
			return code;
		}

		public String getBody() {
			return body;
		}

	}

}
