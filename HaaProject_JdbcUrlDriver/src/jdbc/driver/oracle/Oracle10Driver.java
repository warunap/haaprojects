/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-9 上午11:18:29
 */
package jdbc.driver.oracle;

import java.sql.SQLException;

import jdbc.driver.OracleDriver;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class Oracle10Driver extends OracleDriver {

	protected String getJdbcType() throws SQLException {
		return "oracle10";
	}

}
