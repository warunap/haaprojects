package jdbc.driver.oracle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import jdbc.driver.OracleDriver;

/**
 * Created By: Comwave Project Team
 * Created Date: 2012-9-14 下午4:47:47
 */

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestOralce7 {

	public static void main(String[] args) throws Exception {
		Class.forName("jdbc.driver.OracleDriver");
		DriverManager.registerDriver(new OracleDriver());
		testOracle7();
		// testOracle10();
	}

	private static void testOracle7() throws Exception {
		Connection conn = DriverManager.getConnection("oracle7:@192.168.5.168:1521:RT", "stdba", "stdba");

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select ROAD from rall where rownum<5");
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

		// output data
		StringBuffer buffer = new StringBuffer();
		while (rs.next()) {
			buffer.append("<row>");
			for (int i = 1; i <= numColumns; i++) {
				Object data = rs.getObject(i);
				buffer.append("<column>");
				buffer.append("<name>" + rmeta.getColumnName(i).toUpperCase() + "</name>");
				String value = "";
				if (data != null) {
					value = data.toString().trim();
				}
				buffer.append("<value>" + value + "</value>");
				buffer.append("</column>");
			}
			buffer.append("</row>");
		}

		System.out.println(buffer.toString());

		// int result = stmt.executeUpdate("update rall set city='good1' ");
		// System.out.println(result);
	}

	private static void testOracle10() throws Exception {
		Connection conn = DriverManager.getConnection("oracle10:@192.168.5.74:1521:orcl", "rtmartvts", "comwave");

		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT * FROM CITY");
		while (rset.next())
			System.out.println(rset.getString(1));

	}
}
