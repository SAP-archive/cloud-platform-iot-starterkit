package com.sap.iot.starterkit.cert;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class HttpClient {

	public HttpsURLConnection openSSLConnection(String path, SSLSocketFactory sslSocketFactory)
	throws IOException {
		HttpsURLConnection connection = openConnection(path);

		connection.setSSLSocketFactory(sslSocketFactory);

		return connection;
	}

	public void closeConnection(HttpsURLConnection connection) {
		if (connection != null) {
			connection.disconnect();
		}
	}

	public String doPost(HttpsURLConnection connection, String json)
	throws IOException {

		System.out.println("Request body " + json);

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/json");

		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

		OutputStream os = connection.getOutputStream();
		try {
			os.write(bytes);
		}
		finally {
			closeStream(os);
		}

		InputStream is = connect(connection);
		String response = null;
		try {
			response = readFromStream(is);
		}
		finally {
			closeStream(is);
		}

		System.out.println("Response body " + response);
		System.out.println("----------------------------------------------------");

		return response;
	}

	private HttpsURLConnection openConnection(String path)
	throws IOException {

		System.out.println("Connect to " + path);

		URL url = null;
		try {
			url = new URL(path);
		}
		catch (MalformedURLException e) {
			throw new IOException("Invalid HTTPS connection URL specified", e);
		}

		HttpsURLConnection connection = null;
		try {
			connection = (HttpsURLConnection) url.openConnection();
		}
		catch (IOException e) {
			throw new IOException("Unable to open a HTTPS connection", e);
		}

		return connection;
	}

	private InputStream connect(HttpURLConnection connection)
	throws IOException {
		connection.connect();
		if (connection.getResponseCode() > 202) {
			InputStream errorStream = connection.getErrorStream();
			String errorResponse = null;
			try {
				errorResponse = readFromStream(errorStream);
			}
			finally {
				closeStream(errorStream);
			}
			throw new IOException(errorResponse);
		}
		return connection.getInputStream();
	}

	private String readFromStream(InputStream stream)
	throws IOException {
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
			closeStream(stream);
		}
		return sb.toString();
	}

	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (IOException e) {
				System.err.println("Unable to close an I/O stream");
			}
		}
	}

}
