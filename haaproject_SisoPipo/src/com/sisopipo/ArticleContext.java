/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 17, 2010 11:21:53 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 17, 2010 11:21:53 AM
 *
 */
package com.sisopipo;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import c4j.file.FileUtil;
import com.sisopipo.factory.ArticleFactory;
import com.sisopipo.publisher.ArticlePublisher;
import com.sisopipo.publisher.PublisherFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ArticleContext {
	private static final Log logger = LogFactory.getLog(ArticleContext.class);

	private static final String SisoPipo_CONFIG_FILE = "sisopipo.properties";

	private static int interval = 300;

	private static String administrator;

	private static Set<String> allowedEditors = new HashSet<String>();

	private static Properties sisopipoConfig = new Properties();

	private static String publishType;

	private static ArticlePublisher articlePublisher;

	private static ArticleFactory articleFactory;

	static {
		intial();
	}

	private static void intial() {
		try {
			logger.debug("load " + SisoPipo_CONFIG_FILE);
			sisopipoConfig = FileUtil.readFileAsProperties(SisoPipo_CONFIG_FILE);
			String interv = getConfig("publisher.interval");
			if (interv != null)
				interval = Integer.parseInt(interv);

			String editors = getConfig("publisher.editor.list");
			if (editors != null) {
				String[] arr = editors.split("#");
				for (String editor : arr) {
					allowedEditors.add(editor);
				}
			}

			String admin = getConfig("publisher.administrator");
			if (admin != null) {
				administrator = admin;
			}

			String localBaseDir = getConfig("publisher.local.basedir");
			publishType = getConfig("publisher.type");
			if ("directory".equalsIgnoreCase(publishType)) {
				articlePublisher = PublisherFactory.directoryFactory(localBaseDir);
			} else if ("ftp".equalsIgnoreCase(publishType)) {
				String ftpBaseDir = getConfig("publisher.ftp.basedir");
				String ftpurl = getConfig("publisher.ftp.url");
				int ftpport = Integer.parseInt(getConfig("publisher.ftp.port"));
				String ftpuser = getConfig("publisher.ftp.user");
				String ftppasswd = getConfig("publisher.ftp.password");
				articlePublisher = PublisherFactory.ftpFactory(ftpurl, ftpport, ftpuser, ftppasswd, ftpBaseDir, localBaseDir);
			} else {
				logger.error("Error publisher type " + publishType);
			}

			String factory = getConfig("publisher.article.factory");
			Class c = Class.forName(factory);
			articleFactory = (ArticleFactory) c.newInstance();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private static String getConfig(String key) {
		String v = (String) sisopipoConfig.get(key);
		logger.info(key + "-->" + v);
		return v;
	}

	public static int getInterval() {
		return interval;
	}

	public static String getAdministrator() {
		return administrator;
	}

	public static Set<String> getAllowedEditors() {
		return allowedEditors;
	}

	public static String getPublishType() {
		return publishType;
	}

	public static ArticlePublisher getArticlePublisher() {
		return articlePublisher;
	}

	public static ArticleFactory getArticleFactory() {
		return articleFactory;
	}

}
