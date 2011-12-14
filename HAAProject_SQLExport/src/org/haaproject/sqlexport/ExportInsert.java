/**
 * Created Date: Dec 14, 2011 9:35:31 AM
 */
package org.haaproject.sqlexport;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ExportInsert {

	public static void main(String[] args) throws Exception {
		String driverName = args[0];
		String linkUrl = args[1];
		String userName = args[2];
		String password = args[3];
		String tableName = args[4];
		String querySql = args[5];
		String outputFilePath = "./" + tableName + ".sql";

		System.out.println(driverName);
		System.out.println(linkUrl);
		System.out.println(userName);
		System.out.println(password);
		System.out.println(tableName);
		System.out.println(querySql);
		System.out.println(outputFilePath);

		Class.forName(driverName).newInstance();
		Connection myConn = DriverManager.getConnection(linkUrl, userName, password);
		Statement myStmt = myConn.createStatement();
		ResultSet rs = myStmt.executeQuery(querySql);
		ResultSetMetaData rmeta = rs.getMetaData();
		int numColumns = rmeta.getColumnCount();
		System.out.println("numColumns = " + numColumns);

		// console output
		for (int i = 1; i <= numColumns; i++) {
			if (i < numColumns) {
				System.out.print(rmeta.getColumnName(i) + " , ");
			} else {
				System.out.println(rmeta.getColumnName(i));
			}
		}

		String insertPrefix = "INSERT INTO [" + tableName.toUpperCase() + "] (";
		for (int i = 1; i <= numColumns; i++) {
			insertPrefix += rmeta.getColumnName(i) + ",";
		}
		insertPrefix = insertPrefix.substring(0, insertPrefix.length() - 1) + ") ";

		// output data
		StringBuffer buffer = new StringBuffer();
		while (rs.next()) {
			buffer.append(insertPrefix + " VALUES(");
			for (int i = 1; i <= numColumns; i++) {
				String data = rs.getString(i);
				if (data != null) {
					if (i < numColumns) {
						buffer.append("'" + data.trim() + "',");
					} else {
						buffer.append("'" + rs.getString(i).trim() + "')\r\n");
					}
				} else {
					if (i < numColumns) {
						buffer.append("null,");
					} else {
						buffer.append("null);\r\n");
					}
				}
			}
		}

		FileWriter fileWriter = new FileWriter(outputFilePath);
		fileWriter.write(buffer.toString());
		fileWriter.flush();
		fileWriter.close();
		System.out.println("output file : " + outputFilePath);
		rs.close();
		myStmt.close();
		myConn.close();
	}
}
