package com.sap.iot.starterkit.ui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.sap.iot.starterkit.ui.util.SqlUtil;

/**
 * A class DataServlet provides an API that can be used by IoT application developers to retrieve
 * stored messages from the data source
 */
public class DataServlet
extends AbstractBaseServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Keys for the expected path parameters
	 */
	private static final String KEY_DEVICE_ID = "deviceId";
	private static final String KEY_DEVICE_TYPE_ID = "deviceTypeId";
	private static final String KEY_MESSAGE_TYPE_ID = "messageTypeId";
	private static final String KEY_LIMIT = "limit";
	private static final String KEY_TABLE_NAME = "tableName";

	private DataSource dataSource;

	/**
	 * Initializes the Java servlet
	 */
	@Override
	public void init()
	throws ServletException {
		try {
			dataSource = getResource("jdbc/DefaultDB");
		}
		catch (IOException e) {
			throw new ServletException("Unable to establish a connectivity to the data source", e);
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

		doGetData(request, response);
	}

	/**
	 * Gets content of the DB table as JSON string
	 */
	protected void doGetData(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// check for path parameters and construct properties out of them
		Properties properties = null;
		try {
			properties = buildProperties(request.getPathInfo(), KEY_DEVICE_ID, KEY_DEVICE_TYPE_ID,
				KEY_MESSAGE_TYPE_ID, KEY_LIMIT);
		}
		catch (IllegalArgumentException e) {
			printError(response, e.getMessage());
			return;
		}

		// check if a table with the specified name exists in the data base
		try {
			String tableName = properties.getProperty(KEY_TABLE_NAME);
			if (!isTableExists(tableName)) {
				printError(response,
					"A table with the name [" + tableName +
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
		String[] parts = pathInfo.split("/");
		if (parts.length != keys.length) {
			throw new IllegalArgumentException("Wrong number of path parameters.");
		}

		Properties properties = new Properties();
		for (int i = 0; i < keys.length; i++) {
			properties.put(keys[i], parts[i]);
		}

		properties.put(KEY_TABLE_NAME, String.format(Locale.ENGLISH, "T_IOT_%1$s",
			properties.getProperty(KEY_MESSAGE_TYPE_ID).toUpperCase(Locale.ENGLISH)));

		return properties;
	}

	/**
	 * Opens a connection to the data source
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
		Connection connection = openConnection();
		String driverName = connection.getMetaData().getDatabaseProductName()
			.toLowerCase(Locale.ENGLISH);

		String tableName = properties.getProperty(KEY_TABLE_NAME);
		String sql = SqlUtil.createSelectStatement(driverName);
		sql = String.format(Locale.ENGLISH, sql, tableName, properties.getProperty(KEY_LIMIT));

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
			int lastIndex = sb.lastIndexOf(",");
			if (lastIndex > -1) {
				sb.deleteCharAt(lastIndex);
			}
			sb.append("]");
		}
		catch (SQLException e) {
			throw new SQLException("Unable to select the data for a table with the name [" +
				tableName + "] from the data base", e);
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
			throw new SQLException("Unable to check if a table with the name [" + tableName +
				"] exists in the data base", e);
		}
		finally {
			closeConnection(connection);
		}
	}

}