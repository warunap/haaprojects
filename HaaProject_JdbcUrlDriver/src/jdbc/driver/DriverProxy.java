/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-9 上午11:18:29
 */
package jdbc.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jdbc.JdbcUrlLoader;

/**
 * @author Geln Yang
 * @version 1.0
 */
public abstract class DriverProxy extends JdbcUrlLoader implements Driver {

	private static Map<String, Class<? extends Driver>> driverClazzMap = new HashMap<String, Class<? extends Driver>>();

	/* a instance field */
	private Map<String, Driver> orignalDriverMap = new HashMap<String, Driver>();

	protected abstract String getJdbcType() throws SQLException;

	public abstract String formatUrl(String url);

	public abstract String getJdbcType(String url);

	public abstract void validUrl(String url) throws SQLException;

	/**
	 * Get a new proxy {@link Driver} Object
	 */
	protected Driver getOrignalDriver() throws SQLException {
		return getOrignalDriver(getJdbcType());
	}

	protected Driver getOrignalDriver(String jdbcType) throws SQLException {
		Driver orignalDriver = orignalDriverMap.get(jdbcType);
		if (orignalDriver == null) {
			try {
				orignalDriver = getDriverClazz(jdbcType).newInstance();
				orignalDriverMap.put(jdbcType, orignalDriver);
			} catch (Exception e) {
				throw new SQLException(e.getMessage());
			}
		}
		return orignalDriver;
	}

	protected Class<? extends Driver> getDriverClazz() throws SQLException, ClassNotFoundException {
		return getDriverClazz(getJdbcType());
	}

	protected Class<? extends Driver> getDriverClazz(String jdbcType) throws SQLException, ClassNotFoundException {
		if (jdbcType == null) {
			throw new SQLException("not set the jdbc type!");
		}
		Class<? extends Driver> driverClazz = driverClazzMap.get(jdbcType);
		if (driverClazz == null) {
			System.out.println("try to load jdbc driver:" + jdbcType);
			String nameKey = "jdbc.driver.class.name." + jdbcType;
			Class<?> clazz = getClazz(nameKey);
			driverClazz = clazz.asSubclass(Driver.class);
			driverClazzMap.put(jdbcType, driverClazz);
			if (driverClazz != null) {
				System.out.println(jdbcType + ":" + driverClazz + "@" + Integer.toHexString(driverClazz.hashCode()));
			}
		}

		return driverClazz;
	}

	public Connection connect(String url, Properties info) throws SQLException {
		validUrl(url);
		getJdbcType(url);
		url = formatUrl(url);
		return getOrignalDriver().connect(url, info);
	}

	public boolean acceptsURL(String url) throws SQLException {
		validUrl(url);
		getJdbcType(url);
		url = formatUrl(url);
		return getOrignalDriver().acceptsURL(url);
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return getOrignalDriver().getPropertyInfo(url, info);
	}

	public int getMajorVersion() {
		try {
			return getOrignalDriver().getMajorVersion();
		} catch (SQLException e) {
			// throw new RuntimeException(e);
			return 1;
		}
	}

	public int getMinorVersion() {
		try {
			return getOrignalDriver().getMinorVersion();
		} catch (SQLException e) {
			// throw new RuntimeException(e);
			return 0;
		}
	}

	public int getDatabaseMajorVersion() {
		try {
			return getOrignalDriver().getMajorVersion();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean jdbcCompliant() {
		try {
			return getOrignalDriver().jdbcCompliant();
		} catch (SQLException e) {
			// throw new RuntimeException(e);
			return true;
		}
	}

}
