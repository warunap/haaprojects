package jdbc.driver.oracle;
/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-5 下午6:18:06
 */

/**
 * @author Geln Yang
 * @version 1.0
 */
import jdbc.xa.oracle.Oracle10XADataSource;
import jdbc.xa.oracle.Oracle7XADataSource;

public class XADataSourceTest {

	public static void main(String args[]) throws Exception {
		testOracle7();
		testOracle10();
	}

	private static void testOracle10() throws Exception {
		Oracle10XADataSource datasource = new Oracle10XADataSource();
		System.out.println(datasource);
		System.out.println(datasource.getLoginTimeout());
		datasource.setURL("a");

	}

	private static void testOracle7() throws Exception {
		Oracle7XADataSource datasource = new Oracle7XADataSource();
		System.out.println(datasource);
		System.out.println(datasource.getLoginTimeout());
		datasource.setURL("a");
	}
}