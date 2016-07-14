package com.sap.iot.starterkit.ui;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

/**
 * An abstract dispatcher implementation responsible to forward HTTP requests back and force from
 * client UI to IoT Services
 */
public abstract class AbstractDispatcherServlet
extends AbstractBaseServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Logging API
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDispatcherServlet.class);

	private DestinationConfiguration destinationConfiguration;
	private String destinationUrl;

	/**
	 * Returns a value of the {@code URL} property like specified in the destination file
	 */
	protected abstract String getDestinationUrl()
	throws IOException;

	/**
	 * Returns a name of the destination file
	 */
	protected abstract String getDestinationName();

	/**
	 * Initializes the Java servlet
	 */
	@Override
	public void init()
	throws ServletException {
		try {
			destinationConfiguration = getDestinationConfiguration();
		}
		catch (IOException e) {
			throw new ServletException(e.getMessage(), e);
		}
		try {
			destinationUrl = getDestinationUrl();
		}
		catch (IOException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * Handles client's HTTP GET requests
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			printError(response, "Unsupported operation");
			return;
		}

		URL url = null;
		try {
			url = new URL(destinationUrl + pathInfo);
		}
		catch (MalformedURLException e) {
			printError(response, "Unable to build a URL for HTTP GET request");
			return;
		}

		HttpURLConnection connection = null;
		try {
			connection = openConnection(url);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}

		try {
			forwardGet(connection, request, response);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		finally {
			closeConnection(connection);
		}
	}

	/**
	 * Handles client's HTTP POST requests
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			printError(response, "Unsupported operation");
			return;
		}

		URL url = null;
		try {
			url = new URL(destinationUrl + pathInfo);
		}
		catch (MalformedURLException e) {
			printError(response, "Unable to build a URL for HTTP POST request");
			return;
		}

		HttpURLConnection connection = null;
		try {
			connection = openConnection(url);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}

		try {
			forwardPost(connection, request, response);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		finally {
			closeConnection(connection);
		}
	}

	/**
	 * Returns a concrete destination configuration object from JNDI connectivity configuration
	 */
	protected DestinationConfiguration getDestinationConfiguration()
	throws IOException {
		String name = getDestinationName();
		DestinationConfiguration destination = geConnectivityConfiguration().getConfiguration(name);
		if (destination == null) {
			throw new IOException(
				"Unable to establish a connectivity to the destination [" + name + "]");
		}
		return destination;
	}

	/**
	 * Returns connectivity configuration object from JNDI context
	 */
	protected ConnectivityConfiguration geConnectivityConfiguration()
	throws IOException {
		return getResource("connectivityConfiguration");
	}

	/**
	 * Opens a HTTP URL connection to the destination
	 * 
	 * @param url
	 *            a URL to the destination service
	 * @return a HTTP URL connection
	 * @throws IOException
	 *             - if fails to open a connection
	 */
	private HttpURLConnection openConnection(URL url)
	throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		}
		catch (IOException e) {
			throw new IOException("Unable to open a HTTP URL connection to the destination [" +
				destinationConfiguration.getProperty("Name") + "]", e);
		}
		String user = destinationConfiguration.getProperty("User");
		String password = destinationConfiguration.getProperty("Password");
		if (user == null || password == null || user.trim().isEmpty() ||
			password.trim().isEmpty()) {
			throw new IOException("User credentails are not specified for the destination [" +
				destinationConfiguration.getProperty("Name") + "]");
		}
		@SuppressWarnings("restriction")
		String base64 = new sun.misc.BASE64Encoder()
			.encode((user + ":" + password).getBytes("UTF-8"));
		connection.setRequestProperty("Authorization", "Basic " + base64);
		return connection;
	}

	/**
	 * Closes a HTTP URL connection
	 * 
	 * @param connection
	 *            a URL connection to be closed
	 */
	private void closeConnection(HttpURLConnection connection) {
		if (connection != null) {
			connection.disconnect();
		}
	}

	/**
	 * Closes this stream quietly and releases any system resources associated with it
	 * 
	 * @param stream
	 *            a stream to close
	 */
	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (IOException e) {
				LOGGER.error("Unable to close an I/O stream", e);
			}
		}
	}

	/**
	 * Copies (writes) an input stream to an output stream
	 * 
	 * @param is
	 *            an input stream to copy from
	 * @param os
	 *            an output stream to copy to
	 * @throws IOException
	 *             - if copying operation fail
	 */
	private void copyStream(InputStream is, OutputStream os)
	throws IOException {
		try {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		}
		catch (IOException e) {
			throw new IOException(
				"Unable to copy the content of an input stream into an output stream", e);
		}
	}

	/**
	 * Forwards HTTP POST requests
	 */
	private void forwardPost(HttpURLConnection connection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		// prepare HTTP POST
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", request.getContentType());

		handleHttpRequest(connection, request, response);
		handleHttpResponse(connection, request, response);
	}

	/**
	 * Forwards HTTP GET requests
	 */
	private void forwardGet(HttpURLConnection connection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		// prepare HTTP GET
		connection.setUseCaches(false);
		connection.setRequestMethod("GET");

		handleHttpResponse(connection, request, response);
	}

	/**
	 * Handles an actual HTTP request from <b>Starter Kit UI</b> and pushes its content further to
	 * the backend side <b>MMS/RDMS</b>
	 */
	private void handleHttpRequest(HttpURLConnection connection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = request.getInputStream();
			os = connection.getOutputStream();
			copyStream(is, os);
			os.flush();
		}
		catch (IOException e) {
			LOGGER.error("Unable to handle the HTTP request", e);
			throw e;
		}
		finally {
			closeStream(is);
			closeStream(os);
		}
	}

	/**
	 * Handles an actual HTTP response from <b>MMS/RDMS</b> and pushes its content back to the
	 * client side <b>Starter Kit UI</b>
	 */
	private void handleHttpResponse(HttpURLConnection connection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		int responseCode = connection.getResponseCode();
		String contentType = connection.getContentType();

		// copy headers
		response.setStatus(responseCode);
		response.setContentType(contentType);

		// copy body
		InputStream is = null;
		OutputStream os = null;
		try {
			if (responseCode > HttpServletResponse.SC_ACCEPTED) {
				is = connection.getErrorStream();
			}
			else {
				is = connection.getInputStream();
			}
			os = response.getOutputStream();
			copyStream(is, os);
			os.flush();
		}
		catch (IOException e) {
			LOGGER.error("Unable to handle the HTTP response", e);
			throw e;
		}
		finally {
			closeStream(is);
			closeStream(os);
		}
	}

}