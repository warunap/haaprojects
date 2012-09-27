/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-9 上午11:18:29
 */
package jdbc.driver;

import java.sql.DriverManager;
import java.sql.SQLException;

import jdbc.driver.oracle.Oracle10Driver;
import jdbc.driver.oracle.Oracle7Driver;

/**
 * Use the driver of exact version like {@link Oracle10Driver}, {@link Oracle7Driver} if using different version drivers at the same time.
 * 
 * @author Geln Yang
 * @version 1.0
 */
public class OracleDriver extends DriverProxy {

	static {
		try {
			/* initialize jdbc driver order */
			DriverManager.registerDriver(new Oracle10Driver());
			DriverManager.registerDriver(new Oracle7Driver());
		} catch (Exception e) {
		}
	}

	protected String jdbcType;

	public void validUrl(String url) throws SQLException {
		if (url == null) {
			throw new SQLException("null jdbc url!");
		}
		if (!url.toLowerCase().matches("oracle(\\d+):@.*")) {
			throw new SQLException("unexpected jdbc url, expect start with 'oracle[VERSION]:@'");
		}
	}

	public String formatUrl(String url) {
		return "jdbc:oracle:thin:" + url.substring(url.indexOf("@"));
	}

	protected String getJdbcType() throws SQLException {
		return jdbcType;
	}

	public String getJdbcType(String url) {
		jdbcType = url.substring(0, url.indexOf(":"));
		return jdbcType;
	}

}
