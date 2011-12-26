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
public class ExportCSV {

	public static void main(String[] args) throws Exception {
		String driverName = args[0];
		String linkUrl = args[1];
		String userName = args[2];
		String password = args[3];
		String tableName = args[4];
		String querySql = args[5];
		String outputFilePath = "./" + tableName + ".csv";

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

		String head = "";
		for (int i = 1; i <= numColumns; i++) {
			head += rmeta.getColumnName(i) + ",";
		}
		head = head.substring(0, head.length() - 1)+"\r\n" ;

		// output data
		StringBuffer buffer = new StringBuffer();
		buffer.append(head);
		int count = 0;
		while (rs.next()) {
			count++;
			for (int i = 1; i <= numColumns; i++) {
				String data = rs.getString(i);
				if (data != null) {
					if (i < numColumns) {
						buffer.append("\"" + data.trim() + "\",");
					} else {
						buffer.append("\"" + rs.getString(i).trim() + "\"\r\n");
					}
				} else {
					if (i < numColumns) {
						buffer.append("null,");
					} else {
						buffer.append("null\r\n");
					}
				}
			}
		}

		System.out.println("Record count: " + count);
		System.out.println("start to write into file ... ");
		FileWriter fileWriter = new FileWriter(outputFilePath);
		fileWriter.write(buffer.toString());
		fileWriter.flush();
		fileWriter.close();
		System.out.println("end to write into file ... ");
		rs.close();
		myStmt.close();
		myConn.close();
		System.out.println("------------------------");
		System.out.println("over");
	}
}
