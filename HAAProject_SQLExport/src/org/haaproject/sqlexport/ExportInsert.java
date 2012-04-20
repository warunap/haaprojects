/**
 * Created Date: Dec 14, 2011 9:35:31 AM
 */
package org.haaproject.sqlexport;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

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
		String saveFileName = args[5];
		String querySql = args[6];
		String outputFilePath = "./" + saveFileName + ".sql";

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
		System.out.println("column count: " + numColumns);
		System.out.println("-------------------------------------");
		for (int i = 1; i <= numColumns; i++) {
			if (i < numColumns) {
				System.out.print(rmeta.getColumnName(i) + " , ");
			} else {
				System.out.println(rmeta.getColumnName(i));
			}
		}
		System.out.println("-------------------------------------");

		String insertSqlPrefix = "INSERT INTO " + tableName.toUpperCase() + " (";
		for (int i = 1; i <= numColumns; i++) {
			insertSqlPrefix += rmeta.getColumnName(i) + ",";
		}
		insertSqlPrefix = insertSqlPrefix.substring(0, insertSqlPrefix.length() - 1) + ") VALUES(";

		// output data
		StringBuffer buffer = new StringBuffer();
		int count = 0;
		while (rs.next()) {
			count++;
			buffer.append(insertSqlPrefix);
			for (int i = 1; i <= numColumns; i++) {
				String data = rs.getString(i);
				if (data != null) {
					if (i < numColumns) {
						buffer.append("'" + data.trim() + "',");
					} else {
						buffer.append("'" + rs.getString(i).trim() + "');\r\n");
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

		System.out.println("Record count: " + count);
		System.out.println("start to write into file ... ");
		FileUtils.writeStringToFile(new File(outputFilePath), buffer.toString(), "UTF-8");
		System.out.println("end to write into file ... ");
		rs.close();
		myStmt.close();
		myConn.close();
		System.out.println("------------------------");
		System.out.println("over");
	}
}
