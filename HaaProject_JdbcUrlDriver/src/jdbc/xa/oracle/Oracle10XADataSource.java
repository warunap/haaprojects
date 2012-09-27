/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-9 下午4:45:10
 */
package jdbc.xa.oracle;

import java.sql.SQLException;

import jdbc.xa.XADataSourceProxy;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class Oracle10XADataSource extends XADataSourceProxy {

	protected String getJdbcType() throws SQLException {
		return "oracle10";
	}

}
