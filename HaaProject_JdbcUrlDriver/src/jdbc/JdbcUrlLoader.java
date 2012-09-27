/**
 * Created By: gelnyang@gmail.com
 * Created Date: 2012-9-9 下午4:19:32
 */
package jdbc;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Geln Yang
 * @version 1.0
 */
public abstract class JdbcUrlLoader {

	private static Properties properties;

	static {
		try {
			InputStream is = JdbcUrlLoader.class.getClassLoader().getResourceAsStream("jdbcdriverurl.properties");
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String getConfig(String key) {
		return properties.getProperty(key);
	}

	protected abstract String getJdbcType() throws SQLException;

	protected URLClassLoader getJdbcClasssLoader() throws SQLException {
		String jdbcType = getJdbcType();
		String urlKey = "jdbc.jar.url." + jdbcType;
		String jdbcClazzUrl = getConfig(urlKey);
		if (jdbcClazzUrl == null || jdbcClazzUrl.trim().length() == 0) {
			throw new SQLException("no config:" + urlKey);
		}
		System.out.println(urlKey + "=" + jdbcClazzUrl);

		try {
			return new URLClassLoader(new URL[] { new URL(jdbcClazzUrl) });
		} catch (MalformedURLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	protected Class<?> getClazz(String key) throws SQLException, ClassNotFoundException {
		String clazzName = properties.getProperty(key);
		if (clazzName == null || clazzName.trim().length() == 0) {
			throw new SQLException("No config:" + key);
		}
		System.out.println(key + "=" + clazzName);
		return getJdbcClasssLoader().loadClass(clazzName);
	}

}
