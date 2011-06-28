/**
 * Created Date: Jun 13, 2011 2:27:15 PM
 */
package org.haaproject.mail;

import java.util.Properties;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class HaaMailConfig {
	private static Properties properties = new Properties();
	static {
		try {
			properties.load(HaaMailConfig.class.getResourceAsStream("/haaproject_mail.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getConfig(String key) {
		return properties.getProperty(key);
	}
}
