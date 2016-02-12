package com.sap.iot.starterkit.ui;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

/**
 * A class DataServlet provides an API that can be used by IoT application developers to retrieve
 * stored messages from the data source and push messages to the device via IoT MMS.
 */
public class DataServlet
extends HttpServlet {

	/**
	 * A default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logging API.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DataServlet.class);

	/**
	 * Key values for the expected path parameters
	 */
	private static final String KEY_DEVICE_ID = "deviceId";
	private static final String KEY_DEVICE_TYPE_ID = "deviceTypeId";
	private static final String KEY_MESSAGE_TYPE_ID = "messageTypeId";

	/**
	 * A factory for connections to the physical data source
	 */
	private DataSource dataSource;

	/**
	 * A configuration for data communication via HTTP destination with IoT MMS
	 */
	private DestinationConfiguration destinationConfigurationMMS;

	/**
	 * A configuration for data communication via HTTP destination with IoT RDMS
	 */
	private DestinationConfiguration destinationConfigurationRDMS;

	/**
	 * Initializes the Java Servlet
	 */
	@Override
	public void init()
	throws ServletException {
		try {
			InitialContext initialContext = new InitialContext();
			dataSource = (DataSource) initialContext.lookup("java:comp/env/jdbc/DefaultDB");

			ConnectivityConfiguration connectivityConfiguration = (ConnectivityConfiguration) initialContext
				.lookup("java:comp/env/connectivityConfiguration");
			destinationConfigurationMMS = connectivityConfiguration.getConfiguration("iotmms");
			destinationConfigurationRDMS = connectivityConfiguration.getConfiguration("iotrdms");
		}
		catch (NamingException e) {
			throw new ServletException("Unable to establish a connectivity to the data source");
		}
		if (destinationConfigurationMMS == null) {
			throw new ServletException(
				"Unable to establish a connectivity to the IoT MMS destination. Check your 'iotmms' destination.");
		}
		if (destinationConfigurationRDMS == null) {
			throw new ServletException(
				"Unable to establish a connectivity to the IoT RDMS destination. Check your 'iotrdms' destination.");
		}
	}

	/**
	 * Handles HTTP GET request from a client
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			printError(response, "Unsupported operation");
			return;
		}
		// get data of the specific table
		if (pathInfo.startsWith("/table")) {
			doGetData(request, response);
		}
		// get all registered devices
		else if (pathInfo.startsWith("/devices")) {
			doGetDevices(request, response);
		}
		// get all message types
		else if (pathInfo.startsWith("/messagetypes")) {
			doGetMessageTypes(request, response);
		}
		else {
			printError(response, "Unsupported operation");
		}
	}

	/**
	 * Gets a content of the DB table as JSON string
	 */
	protected void doGetData(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// check for path parameters and construct properties out of them
		Properties properties = null;
		try {
			properties = buildProperties(request.getPathInfo(), KEY_DEVICE_ID, KEY_DEVICE_TYPE_ID,
				KEY_MESSAGE_TYPE_ID);
		}
		catch (IllegalArgumentException e) {
			printError(response, e.getMessage());
			return;
		}
		// check if a table with the specified name exists in the data base
		String tableName = buildTableName(properties);
		try {
			if (!isTableExists(tableName)) {
				printError(response, "A table with the name [" + tableName +
					"] does not exist in the data base. Please, send some messages of type '" +
					properties.getProperty(KEY_MESSAGE_TYPE_ID) +
					"' first on behalf of the device.");
				return;
			}
		}
		catch (SQLException e) {
			printError(response, e.getMessage());
			return;
		}
		// execute SQL select to get the table contents and build JSON string out of it
		String tableData = null;
		try {
			tableData = selectTableData(properties);
		}
		catch (SQLException e) {
			printError(response, e.getMessage());
			return;
		}
		// respond with the result
		printJson(response, tableData);
	}

	/**
	 * Gets all registered devices as JSON string
	 */
	protected void doGetDevices(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// build a HTTP URL referring to the destination
		String destinationURL = destinationConfigurationRDMS.getProperty("URL");
		if (destinationURL.endsWith("/")) {
			destinationURL = destinationURL.substring(0, destinationURL.length() - 1);
		}
		destinationURL = destinationURL.concat("/devices");
		URL url = null;
		try {
			url = new URL(destinationURL);
		}
		catch (MalformedURLException e) {
			printError(response,
				"Unable to build a HTTP URL for the IoT RDMS Registered Devices API request");
			return;
		}
		// open a HTTP connection to the destination
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = openURLConnection(url, destinationConfigurationRDMS);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		// forward an original request and handle the destination response
		try {
			forwardGet(httpURLConnection, request, response);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		finally {
			closeURLConnection(httpURLConnection);
		}
	}

	/**
	 * Gets all registered message types as JSON string
	 */
	protected void doGetMessageTypes(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// build a HTTP URL referring to the destination
		String destinationURL = destinationConfigurationRDMS.getProperty("URL");
		if (destinationURL.endsWith("/")) {
			destinationURL = destinationURL.substring(0, destinationURL.length() - 1);
		}
		destinationURL = destinationURL.concat("/messagetypes");
		URL url = null;
		try {
			url = new URL(destinationURL);
		}
		catch (MalformedURLException e) {
			printError(response,
				"Unable to build a HTTP URL for the IoT RDMS Message Types API request");
			return;
		}
		// open a HTTP connection to the destination
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = openURLConnection(url, destinationConfigurationRDMS);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		// forward an original request and handle the destination response
		try {
			forwardGet(httpURLConnection, request, response);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		finally {
			closeURLConnection(httpURLConnection);
		}
	}

	/**
	 * Handles HTTP POST request from a client
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			printError(response, "Unsupported operation");
			return;
		}
		// push message to the device
		if (pathInfo.startsWith("/push")) {
			doPushData(request, response);
		}
		else {
			printError(response, "Unsupported operation");
		}
	}

	/**
	 * Pushes messages to the device
	 */
	protected void doPushData(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// check for path parameters and construct properties out of them
		Properties properties = null;
		try {
			properties = buildProperties(request.getPathInfo(), KEY_DEVICE_ID);
		}
		catch (IllegalArgumentException e) {
			printError(response, e.getMessage());
			return;
		}
		// build a HTTP URL referring to the destination
		String destinationURL = destinationConfigurationMMS.getProperty("URL");
		if (destinationURL.endsWith("/")) {
			destinationURL = destinationURL.substring(0, destinationURL.length() - 1);
		}
		// backward compatibility
		if (!destinationURL.endsWith("http/push")) {
			destinationURL = destinationURL.concat("/http/push");
		}
		destinationURL = destinationURL.concat("/").concat(properties.getProperty(KEY_DEVICE_ID));
		URL url = null;
		try {
			url = new URL(destinationURL);
		}
		catch (MalformedURLException e) {
			printError(response, "Unable to build a HTTP URL for the IoT MMS Push API request");
			return;
		}
		// open a HTTP connection to the destination
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = openURLConnection(url, destinationConfigurationMMS);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		// forward an original request and handle the destination response
		try {
			forwardPost(httpURLConnection, request, response);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		finally {
			closeURLConnection(httpURLConnection);
		}
	}

	/*
	 * Common functionality goes here.
	 */

	/**
	 * Build properties set out of the HTTP request path string
	 * 
	 * @param pathInfo
	 *            a HTTP request path string
	 * @param keys
	 *            an order of path parameters expected in the request
	 * @return a properties set
	 * @throws IllegalArgumentException
	 *             if wrong number of path parameters were received in the request
	 */
	private Properties buildProperties(String pathInfo, String... keys) {
		pathInfo = pathInfo.replaceFirst("/", "");
		pathInfo = pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.length());
		String[] parts = pathInfo.split("/");
		if (parts.length != keys.length) {
			throw new IllegalArgumentException("Wrong number of path parameters.");
		}
		Properties properties = new Properties();
		for (int i = 0; i < keys.length; i++) {
			properties.put(keys[i], parts[i]);
		}
		return properties;
	}

	/**
	 * Opens a connection to the data source
	 * 
	 * @return a connection to the data source
	 * @throws SQLException
	 *             if fails to open a connection
	 */
	private Connection openDSConnection()
	throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		}
		catch (SQLException e) {
			throw new SQLException("Unable to open a connection to the data source", e);
		}
		return connection;
	}

	/**
	 * Closes a connection to the data source quietly
	 * 
	 * @param connection
	 *            a connection to be closed
	 * @throws SQLException
	 *             if fails to close a connection
	 */
	private void closeDSConnection(Connection connection)
	throws SQLException {
		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				throw new SQLException("Failed to close a connection to the data source.", e);
			}
		}
	}

	/**
	 * Selects the data for the given Message Type ID filtered by Device ID from the data base. All
	 * entries will have the DESC sorting order according to 'G_CREATED' column value which is added
	 * to all IoT tables by default.
	 * 
	 * @param properties
	 *            a set of path parameters
	 * @return a JSON array with JSON objects containing the table column-value pairs represented as
	 *         JSON string
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	private String selectTableData(Properties properties)
	throws SQLException {
		String tableName = buildTableName(properties);
		String sql = String.format("SELECT * FROM %1$s WHERE G_DEVICE = ? ORDER BY G_CREATED DESC",
			tableName);
		Connection connection = openDSConnection();
		StringBuilder sb = new StringBuilder();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, properties.getProperty(KEY_DEVICE_ID));
			ResultSet resultSet = preparedStatement.executeQuery();
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			List<String> columnNames = new ArrayList<String>();
			for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
				columnNames.add(resultSetMetaData.getColumnName(i + 1));
			}
			sb.append("[");
			while (resultSet.next()) {
				sb.append("{");
				for (int i = 0; i < columnNames.size(); i++) {
					String columnName = columnNames.get(i);
					Object columnValue = resultSet.getObject(i + 1);
					sb.append("\"").append(columnName).append("\"");
					sb.append(":");
					sb.append("\"").append(columnValue).append("\"");
					if (i != (columnNames.size() - 1)) {
						sb.append(",");
					}
				}
				sb.append("}");
				sb.append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("]");
		}
		catch (SQLException e) {
			throw new SQLException("Unable to select the data for a table with the name [" +
				tableName + "] from the data base", e);
		}
		finally {
			closeDSConnection(connection);
		}
		return sb.toString();
	}

	/**
	 * Builds an IoT table name out of the HTTP request path parameters. Device Type ID and Message
	 * Type ID are expected only. All IoT tables have the next pattern for their names
	 * 'T_IOT_%message_type_id(in UPPER case)%'.
	 * 
	 * @param properties
	 *            a set of path parameters
	 * @return a table name
	 */
	private String buildTableName(Properties properties) {
		return String.format("T_IOT_%1$s", properties.get(KEY_MESSAGE_TYPE_ID).toString()
			.toUpperCase());
	}

	/**
	 * Checks if a table with a given name exists in the data base.
	 * 
	 * @param tableName
	 *            a data base table name
	 * @return true in case a table exists in the data base, otherwise false
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	private boolean isTableExists(String tableName)
	throws SQLException {
		Connection connection = openDSConnection();
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getTables(null, null, tableName, null);
			while (resultSet.next()) {
				String nextTableName = resultSet.getString("TABLE_NAME");
				if (tableName.equals(nextTableName)) {
					return true;
				}
			}
			return false;
		}
		catch (SQLException e) {
			throw new SQLException("Unable to check if a table with the name [" + tableName +
				"] exists in the data base", e);
		}
		finally {
			closeDSConnection(connection);
		}
	}

	/**
	 * Opens a HTTP URL connection to the destination.
	 * 
	 * @param url
	 *            a URL to the destination service
	 * @param destinationConfiguration
	 *            a destination configuration instance
	 * @return a HTTP URL connection
	 * @throws IOException
	 *             if fails to open a connection
	 */
	private HttpURLConnection openURLConnection(URL url,
		DestinationConfiguration destinationConfiguration)
	throws IOException {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
		}
		catch (IOException e) {
			throw new IOException("Unable to open a HTTP URL connection to the destination [" +
				destinationConfiguration.getProperty("Name") + "]", e);
		}
		String user = destinationConfiguration.getProperty("User");
		String password = destinationConfiguration.getProperty("Password");
		if (user == null || password == null || user.trim().isEmpty() || password.trim().isEmpty()) {
			throw new IOException("User credentails are not specified for the destination [" +
				destinationConfiguration.getProperty("Name") + "]");
		}
		@SuppressWarnings("restriction")
		String base64 = new sun.misc.BASE64Encoder().encode((user + ":" + password)
			.getBytes("UTF-8"));
		urlConnection.setRequestProperty("Authorization", "Basic " + base64);
		return urlConnection;
	}

	/**
	 * Closes a HTTP URL connection
	 * 
	 * @param urlConnection
	 *            a URL connection to be closed
	 */
	private void closeURLConnection(HttpURLConnection urlConnection) {
		if (urlConnection != null) {
			urlConnection.disconnect();
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
				logger.error("Unable to close an I/O stream", e);
			}
		}
	}

	/**
	 * Copies (writes) an input stream to an output stream.
	 * 
	 * @param is
	 *            an input stream to copy from
	 * @param os
	 *            an output stream to copy to
	 * @throws IOException
	 *             if copying operation fail
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
			throw new IOException("Unable to copy the content of an stream into an output stream",
				e);
		}
	}

	/**
	 * Forwards HTTP POST requests
	 */
	private void forwardPost(HttpURLConnection urlConnection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		// prepare HTTP POST
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", request.getContentType());

		handleHttpRequest(urlConnection, request, response);
		handleHttpResponse(urlConnection, request, response);
	}

	/**
	 * Forwards HTTP GET requests
	 */
	private void forwardGet(HttpURLConnection urlConnection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		// prepare HTTP GET
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("GET");

		handleHttpResponse(urlConnection, request, response);
	}

	/**
	 * Handles an actual HTTP request from <b>Starter Kit UI</b> and pushes its content further to
	 * the backend side <b>MMS/RDMS</b>
	 */
	private void handleHttpRequest(HttpURLConnection urlConnection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = request.getInputStream();
			os = urlConnection.getOutputStream();
			copyStream(is, os);
			os.flush();
		}
		catch (IOException e) {
			logger.error("Unable to handle the HTTP request", e);
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
	private void handleHttpResponse(HttpURLConnection urlConnection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		int responseCode = urlConnection.getResponseCode();
		String contentType = urlConnection.getContentType();

		// copy headers
		response.setStatus(responseCode);
		response.setContentType(contentType);

		// copy body
		InputStream is = null;
		OutputStream os = null;
		try {
			if (responseCode > HttpServletResponse.SC_ACCEPTED) {
				is = urlConnection.getErrorStream();
			}
			else {
				is = urlConnection.getInputStream();
			}
			os = response.getOutputStream();
			copyStream(is, os);
			os.flush();
		}
		catch (IOException e) {
			logger.error("Unable to handle the HTTP response", e);
			throw e;
		}
		finally {
			closeStream(is);
			closeStream(os);
		}
	}

	/**
	 * Flushes a JSON string output to the client with HTTP 200 code
	 */
	private void printJson(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		print(response, message);
	}

	/**
	 * Flushes a text output to the client with HTTP 500 code
	 */
	private void printError(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		print(response, message);
	}

	/**
	 * Flushes an output to the client using UTF-8 encoding
	 */
	private void print(HttpServletResponse response, String message)
	throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(message);
		writer.flush();
		writer.close();
	}

}