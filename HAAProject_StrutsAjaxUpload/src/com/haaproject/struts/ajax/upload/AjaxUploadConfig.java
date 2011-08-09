/**
 * Created By: Comwave Project Team
 * Created Date: Aug 9, 2011 3:29:16 PM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class AjaxUploadConfig {
	public static final String SESSION_FILE_MAP_KEY = "_session_file_map";

	private static Log LOG = LogFactory.getLog(AjaxUploadConfig.class);

	private static Properties properties = new Properties();
	private static String uploadDir;

	static {
		try {
			properties.load(AjaxUploadConfig.class.getResourceAsStream("/ajax_upload.properties"));
			String uploadDir = getConfig("ajax.file.upload.dir");
			setUploadDir(uploadDir);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getAjaxFileNamePrefix() {
		return getConfig("ajax.file.name.prefix");
	}

	public static String getUploadDir() {
		return uploadDir;
	}

	public static void setUploadDir(String uploadDir) {
		if (StringUtils.isNotEmpty(uploadDir)) {
			if (uploadDir.equals(File.separator)) {
				uploadDir += File.separator;
			}
			AjaxUploadConfig.uploadDir = uploadDir;
			try {
				FileUtils.forceMkdir(new File(uploadDir));
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
	}

	public static String getConfig(String key) {
		return properties.getProperty(key);
	}
}
