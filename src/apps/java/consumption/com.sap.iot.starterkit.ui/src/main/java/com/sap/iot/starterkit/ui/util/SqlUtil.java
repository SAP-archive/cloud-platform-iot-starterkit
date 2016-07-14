package com.sap.iot.starterkit.ui.util;

/**
 * SQL statement utilities
 */
public class SqlUtil {

	/**
	 * 
	 * @param driverName
	 * @return
	 */
	public static String createSelectStatement(String driverName) {
		String statement = "SELECT * FROM %1$s WHERE G_DEVICE = ? ORDER BY G_CREATED DESC";
		if (driverName == null) {
			return statement;
		}

		if (driverName.contains("derby")) {
			return "SELECT * FROM %1$s WHERE G_DEVICE = ? ORDER BY G_CREATED DESC FETCH NEXT %2$s ROWS ONLY";
		}
		else if (driverName.contains("sap db")) {
			return "SELECT * FROM %1$s WHERE G_DEVICE = ? ORDER BY G_CREATED DESC LIMIT 0, %2$s";
		}
		else if (driverName.contains("hdb")) {
			return "SELECT * FROM %1$s WHERE G_DEVICE = ? ORDER BY G_CREATED DESC LIMIT %2$s OFFSET 0";
		}
		else if (driverName.contains("adaptive server enterprise")) {
			return "SELECT TOP %2$s * FROM %1$s WHERE G_DEVICE = ? ORDER BY G_CREATED DESC";
		}

		return statement;
	}

}
