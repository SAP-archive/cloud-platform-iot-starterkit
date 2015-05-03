package com.sap.iot.starterkit.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

/**
 * A class DataServlet provides an API that can be used by IoT application developers to retrieve
 * stored messages from the data source and push messages to the device via IoT MMS.
 */
public class DataServlet
extends HttpServlet {

	/**
	 * A default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A factory for connections to the physical data source.
	 */
	private DataSource dataSource;

	/**
	 * A configuration for data communication via HTTP destination.
	 */
	private DestinationConfiguration destinationConfiguration;

	/**
	 * Initialized the Java Servlet.
	 */
	@Override
	public void init()
	throws ServletException {
		try {
			InitialContext initialContext = new InitialContext();
			dataSource = (DataSource) initialContext.lookup("java:comp/env/jdbc/DefaultDB");

			ConnectivityConfiguration connectivityConfiguration = (ConnectivityConfiguration) initialContext
				.lookup("java:comp/env/connectivityConfiguration");
			destinationConfiguration = connectivityConfiguration.getConfiguration("iotmms");
		}
		catch (NamingException e) {
			throw new ServletException("Failed to establish a connectivity to the data source.");
		}
		if (destinationConfiguration == null) {
			throw new ServletException("Failed to establish a connectivity to the destination.");
		}
	}

	/**
	 * Handles HTTP GET request from a client.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// check for path parameters and construct a table name out of them
		String tableName = null;
		try {
			tableName = buildTableNameFromRequest(request);
		}
		catch (IllegalArgumentException e) {
			printError(response, e.getMessage());
			return;
		}
		// check if a table with the specified name exists in the data base
		try {
			if (!isTableExists(tableName)) {
				printError(response, "A table with the name [" + tableName +
					"] does not exist in the data base.");
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
			tableData = selectTableData(tableName);
		}
		catch (SQLException e) {
			printError(response, e.getMessage());
			return;
		}
		// respond with the result
		printJson(response, tableData);
	}

	/*
	 * HTTP GET relevant functionality goes here.
	 */

	/**
	 * Selects the data for the given table name from the data base. All entries will have the DESC
	 * sorting order according to 'G_CREATED' column value which is added to all IoT tables by
	 * default.
	 * 
	 * @param tableName
	 *            a data base table name
	 * @return a JSON array with JSON objects containing the table column-value pairs represented as
	 *         JSON string
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	private String selectTableData(String tableName)
	throws SQLException {
		Connection connection = openConnection();
		StringBuilder sb = new StringBuilder();
		try {
			String sql = String.format("SELECT * FROM %1$s ORDER BY G_CREATED DESC", tableName);
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
			throw new SQLException("Failed to select the data for a table with the name [" +
				tableName + "] from the data base.", e);
		}
		finally {
			closeConnection(connection);
		}
		return sb.toString();
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
		Connection connection = openConnection();
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
			throw new SQLException("Failed to check if a table with the name [" + tableName +
				"] exists in the data base.", e);
		}
		finally {
			closeConnection(connection);
		}
	}

	/**
	 * Opens a connection to the data source.
	 * 
	 * @return a connection to the data source
	 * @throws SQLException
	 *             if fails to open a connection
	 */
	private Connection openConnection()
	throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		}
		catch (SQLException e) {
			throw new SQLException("Failed to open a connection to the data source.", e);
		}
		return connection;
	}

	/**
	 * Closes a connection to the data source.
	 * 
	 * @param connection
	 *            a connection to be closed
	 * @throws SQLException
	 *             if fails to close a connection
	 */
	private void closeConnection(Connection connection)
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
	 * Builds an IoT table name out of the HTTP request path parameters. Device Type ID and Message
	 * Type ID are expected only. All IoT tables have the next pattern for their names
	 * 'T_IOT_%device_type_id(in UPPER case)%_%message_type_id%'.
	 * 
	 * @param request
	 *            a HTTP servlet request instance
	 * @return a table name
	 * @throws IllegalArgumentException
	 *             if no or wrong number of path parameters were received in the request
	 */
	private String buildTableNameFromRequest(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			throw new IllegalArgumentException(
				"Missing path parameters. Device Type ID and Message Type ID are expected.");
		}
		pathInfo = pathInfo.replaceFirst("/", "");
		String[] parts = pathInfo.split("/");
		if (parts.length != 2) {
			throw new IllegalArgumentException(
				"Wrong number of path parameters. Device Type ID and Message Type ID are expected.");
		}
		String deviceId = parts[0];
		String messageTypeId = parts[1];
		return String.format("T_IOT_%1$s_%2$s", deviceId, messageTypeId);
	}

	/**
	 * Handles HTTP POST request from a client.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// check for path parameters and build a HTTP URL referring to the destination
		URL url = null;
		try {
			url = buildURLFromRequest(request);
		}
		catch (IllegalArgumentException e) {
			printError(response, e.getMessage());
			return;
		}
		catch (MalformedURLException e) {
			printError(response, e.getMessage());
			return;
		}
		// open a HTTP connection to the destination
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = openURLConnection(url);
		}
		catch (IOException e) {
			printError(response, e.getMessage());
			return;
		}
		// forward an original request and handle the destination response
		try {
			forwardRequest(httpURLConnection, request, response);
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
	 * HTTP POST relevant functionality goes here.
	 */

	/**
	 * Forwards an original HTTP request to the destination, handles the response and transmits it
	 * back to a client.
	 * 
	 * @param urlConnection
	 *            a URL connection instance
	 * @param request
	 *            a HTTP servlet request instance
	 * @param response
	 *            a HTTP servlet response instance
	 * @throws IOException
	 *             if fails to forward the request or handle the response properly
	 */
	private void forwardRequest(HttpURLConnection urlConnection, HttpServletRequest request,
		HttpServletResponse response)
	throws IOException {
		// prepare for HTTP POST
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", request.getContentType() +
			";charset=UTF-8");

		// copy the content of an original request to the destination request
		InputStream ois = null;
		OutputStream fos = null;
		try {
			ois = request.getInputStream();
			fos = urlConnection.getOutputStream();
			copy(ois, fos);
			fos.flush();
		}
		catch (IOException e) {
			throw new IOException("Failed to forward an original request to the destination.", e);
		}
		finally {
			if (ois != null) {
				try {
					ois.close();
				}
				catch (IOException e) {
					throw new IOException("Failed to close an output stream.", e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				}
				catch (IOException e) {
					throw new IOException("Failed to close an input stream.", e);
				}
			}
		}

		// reset HTTP code and content type
		response.setStatus(urlConnection.getResponseCode());
		response.setContentType(urlConnection.getContentType());

		// copy the content of the destination response to an original response
		InputStream fis = null;
		OutputStream oos = null;
		try {
			fis = urlConnection.getInputStream();
			oos = response.getOutputStream();
			copy(fis, oos);
			oos.flush();
		}
		catch (IOException e) {
			throw new IOException("Failed to forward a destination response to an origin.", e);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException e) {
					throw new IOException("Failed to close an input stream.", e);
				}
			}
			if (oos != null) {
				try {
					oos.close();
				}
				catch (IOException e) {
					throw new IOException("Failed to close an output stream.", e);
				}
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
	private void copy(InputStream is, OutputStream os)
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
				"Failed to copy the input stream's content into an output stream.", e);
		}
	}

	/**
	 * Opens a connection to the destination.
	 * 
	 * @param url
	 *            a URL to the destination service
	 * @return a URL connection
	 * @throws IOException
	 *             if fails to open a connection
	 */
	private HttpURLConnection openURLConnection(URL url)
	throws IOException {
		String proxyHost = System.getProperty("http.proxyHost");
		int proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection(proxy);
		}
		catch (IOException e) {
			throw new IOException("Failed to open a HTTP URL connection to the destination.", e);
		}
		String user = destinationConfiguration.getProperty("User");
		String password = destinationConfiguration.getProperty("Password");
		@SuppressWarnings("restriction")
		String base64 = new sun.misc.BASE64Encoder().encode((user + ":" + password).getBytes());
		urlConnection.setRequestProperty("Authorization", "Basic " + base64);
		return urlConnection;
	}

	/**
	 * Closes a connection to the destination.
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
	 * Builds a URL for the internal HTTP request out of the original HTTP request path parameters.
	 * Device ID is expected only.
	 * 
	 * @param request
	 *            a HTTP servlet request instance
	 * @return a final URL to be used for the HTTP internal request
	 * @throws IllegalArgumentException
	 *             if no or wrong number of path parameters were received in the request
	 * @throws MalformedURLException
	 *             if a URL cannot be constructed
	 */
	private URL buildURLFromRequest(HttpServletRequest request)
	throws MalformedURLException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			throw new IllegalArgumentException("Missing path parameter. Device ID is expected.");
		}
		pathInfo = pathInfo.replaceFirst("/", "");
		String[] parts = pathInfo.split("/");
		if (parts.length != 1) {
			throw new IllegalArgumentException(
				"Wrong number of path parameters. Device ID is expected.");
		}
		String deviceId = parts[0];
		String destinationURL = destinationConfiguration.getProperty("URL");
		if (destinationURL.endsWith("/")) {
			destinationURL = destinationURL.substring(0, destinationURL.length() - 1);
		}
		try {
			return new URL(destinationURL.concat("/").concat(deviceId));
		}
		catch (MalformedURLException e) {
			throw new MalformedURLException(
				"Failed to build a HTTP URL for the destination request.");
		}
	}

	/*
	 * Common functionality goes here.
	 */

	/**
	 * Flushes a JSON string output to a client with HTTP 200 code.
	 * 
	 * @param response
	 *            a HTTP servlet response instance
	 * @param message
	 *            an JSON string to be sent to a client
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	protected void printJson(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		print(response, message);
	}

	/**
	 * Flushes a text output to a client with HTTP 500 code.
	 * 
	 * @param response
	 *            a HTTP servlet response instance
	 * @param message
	 *            an error message to be sent to a client
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	protected void printError(HttpServletResponse response, String message)
	throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		print(response, message);
	}

	/**
	 * Flushes an output to a client using UTF-8 encoding.
	 * 
	 * @param response
	 *            a HTTP servlet response instance
	 * @param message
	 *            a message to be sent to a client
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	protected void print(HttpServletResponse response, String message)
	throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(message);
		writer.flush();
		writer.close();
	}

}