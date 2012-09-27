package jdbc.driver.oracle;
/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-5 下午6:18:06
 */

/**
 * @author Geln Yang
 * @version 1.0
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import jdbc.driver.oracle.Oracle10Driver;
import jdbc.driver.oracle.Oracle7Driver;

public class JdbcTest {

	public static void main(String args[]) throws Exception {

		testOracle7();
		testOracle10();
	}

	private static void testOracle7() throws Exception {
		// Load Oracle driver
		//DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		// Class.forName("oracle.jdbc.driver.OracleDriver");
		// Class.forName("loader.Oracle7JDBCDriver");
		Class.forName("jdbc.driver.oracle.Oracle7Driver");
		//DriverManager.registerDriver(new Oracle7Driver());

		// Connect to the local database
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.5.168:1521:RT", "stdba", "stdba");

		// Query the employee names
		Statement stmt = conn.createStatement();
		// stmt.execute("create table TEST1(A1 VARCHAR2(50) NOT NULL,A2 DATE, A3 NUMBER(10,2))");
		// ResultSet rset = stmt.executeQuery("SELECT owner, table_name FROM dba_tables");

		// String sql = "SELECT column_name FROM cols WHERE table_name LIKE 'AA%'";
		ResultSet rset = stmt.executeQuery("select STAGE_NO,CLIENT_NO,POINT_START,POINT_ADD,POINT_USE from CLIENT_BONUS where STAGE_NO=2012 and CLIENT_NO=1056");
		// ResultSet rset = stmt.executeQuery(sql);
		// Print the name out
		while (rset.next())
			System.out.println(rset.getString(1));

		// System.out.println(rset.getString(1) + ":" + rset.getString(2));

	}

	private static void testOracle10() throws Exception {
		// Load Oracle driver
		// DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		// Class.forName("oracle.jdbc.driver.OracleDriver");
		// Class.forName("loader.Oracle7JDBCDriver");

		//DriverManager.registerDriver(new Oracle10Driver());

		//Class.forName("jdbc.driver.oracle.Oracle10Driver");
		// Connect to the local database
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.5.74:1521:orcl", "rtmartvts", "comwave");

		// Query the employee names
		Statement stmt = conn.createStatement();
		// stmt.execute("create table TEST1(A1 VARCHAR2(50) NOT NULL,A2 DATE, A3 NUMBER(10,2))");
		// ResultSet rset = stmt.executeQuery("SELECT owner, table_name FROM dba_tables");

		// String sql = "SELECT column_name FROM cols WHERE table_name LIKE 'AA%'";
		ResultSet rset = stmt.executeQuery("SELECT * FROM CITY");
		// ResultSet rset = stmt.executeQuery(sql);
		// Print the name out
		while (rset.next())
			System.out.println(rset.getString(1));

		// System.out.println(rset.getString(1) + ":" + rset.getString(2));

	}
}