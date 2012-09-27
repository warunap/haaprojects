/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-9 下午4:16:15
 */
package jdbc.xa;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import jdbc.JdbcUrlLoader;

/**
 * @author Geln Yang
 * @version 1.0
 */
public abstract class XADataSourceProxy extends JdbcUrlLoader implements XADataSource {

	private static Map<String, Class<? extends XADataSource>> xaDataSourceClazzMap = new HashMap<String, Class<? extends XADataSource>>();

	/* a instance field */
	private Map<String, XADataSource> xaDataSourceMap = new HashMap<String, XADataSource>();

	protected XADataSource getXADataSource() throws SQLException {
		String jdbcType = getJdbcType();
		XADataSource xaDataSource = xaDataSourceMap.get(jdbcType);
		if (xaDataSource == null) {
			try {
				xaDataSource = getXADataSourceClazz().newInstance();
				xaDataSourceMap.put(jdbcType, xaDataSource);
			} catch (Exception e) {
				throw new SQLException(e.getMessage());
			}
		}
		return xaDataSource;
	}

	protected Class<? extends XADataSource> getXADataSourceClazz() throws SQLException, ClassNotFoundException {
		String jdbcType = getJdbcType();
		Class<? extends XADataSource> xaClazz = xaDataSourceClazzMap.get(jdbcType);
		if (xaClazz == null) {
			System.out.println("try to load xa datasource class:" + jdbcType);
			String nameKey = "jdbc.xadatasource.class.name." + jdbcType;
			Class<?> clazz = getClazz(nameKey);
			xaClazz = clazz.asSubclass(XADataSource.class);
			xaDataSourceClazzMap.put(jdbcType, xaClazz);
			if (xaClazz != null) {
				System.out.println("xadatasource class:" + xaClazz + "@" + Integer.toHexString(xaClazz.hashCode()));
			}
		}

		return xaClazz;
	}

	public void setURL(String url) throws SQLException {
		XADataSource dataSource = getXADataSource();
		try {
			Class<? extends XADataSource> clazz = getXADataSourceClazz();
			Method method = clazz.getMethod("setURL", String.class);
			method.invoke(dataSource, url);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	public void setUser(String user) throws SQLException {
		XADataSource dataSource = getXADataSource();
		try {
			Class<? extends XADataSource> clazz = getXADataSourceClazz();
			Method method = clazz.getMethod("setUser", String.class);
			method.invoke(dataSource, user);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	public void setPassword(String password) throws SQLException {
		XADataSource dataSource = getXADataSource();
		try {
			Class<? extends XADataSource> clazz = getXADataSourceClazz();
			Method method = clazz.getMethod("setPassword", String.class);
			method.invoke(dataSource, password);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	public PrintWriter getLogWriter() throws SQLException {
		return getXADataSource().getLogWriter();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		getXADataSource().setLogWriter(out);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		getXADataSource().setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return getXADataSource().getLoginTimeout();
	}

	public XAConnection getXAConnection() throws SQLException {
		return getXADataSource().getXAConnection();
	}

	public XAConnection getXAConnection(String user, String password) throws SQLException {
		return getXADataSource().getXAConnection(user, password);
	}

}
