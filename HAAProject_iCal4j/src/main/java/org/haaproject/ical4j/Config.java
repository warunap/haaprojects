/**
 * Created By: Comwave Project Team
 * Created Date: Jun 13, 2011 2:27:15 PM
 */
package org.haaproject.ical4j;

import java.util.Properties;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class Config {
	private static Properties properties = new Properties();
	static {
		try {
			properties.load(Config.class.getResourceAsStream("/haaproject_ical4j.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getConfig(String key) {
		return properties.getProperty(key);
	}
}
