/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 15, 2010 10:33:09 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 15, 2010 10:33:09 PM
 *
 */
package com.sisopipo.publisher;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import c4j.file.FileUtil;
import com.sisopipo.content.Article;
import com.sisopipo.factory.ArticleFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class PublishThread extends Thread {

	private static final Log logger = LogFactory.getLog(PublishThread.class);

	private static final String SisoPipo_CONFIG_FILE = "sisopipo.properties";

	private int interval = 300;

	private Set<String> allowedEditors = new HashSet<String>();

	private String administrator;

	private Properties sisopipoConfig = new Properties();

	private String publishType;

	private ArticlePublisher articlePublisher;

	private ArticleFactory articleFactory;

	public PublishThread() {
		intial();
	}

	@SuppressWarnings("unchecked")
	private void intial() {
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

			publishType = getConfig("publisher.type");
			if ("directory".equalsIgnoreCase(publishType)) {
				String baseDir = getConfig("publisher.directory.basedir");
				articlePublisher = PublisherFactory.directoryFactory(baseDir);
			} else if ("ftp".equalsIgnoreCase(publishType)) {
				String baseDir = getConfig("publisher.ftp.basedir");
				String ftpurl = getConfig("publisher.ftp.url");
				int ftpport = Integer.parseInt(getConfig("publisher.ftp.port"));
				String ftpuser = getConfig("publisher.ftp.user");
				String ftppasswd = getConfig("publisher.ftp.password");
				articlePublisher = PublisherFactory.ftpFactory(ftpurl, ftpport, ftpuser, ftppasswd, baseDir);
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

	private String getConfig(String key) {
		String v = (String) sisopipoConfig.get(key);
		logger.info(key + ":" + v);
		return v;
	}

	public void run() {
		if (articlePublisher == null) {
			logger.error("No article publisher setting!");
			return;
		}

		if (articleFactory == null) {
			logger.error("No article factory setting!");
			return;
		}

		/* the times to catch exception */
		int errorTime = 0;

		/* the times having published in a group times (10) */
		int groupTime = 0;
		while (true) {
			try {
				logger.debug("start publish");
				publish();
			} catch (Exception e) {
				errorTime++;
				logger.error(e);
			}

			groupTime++;

			try {
				Thread.sleep((interval + errorTime) * 1000);

				/* reduce error times in a group time */
				if (groupTime >= 10) {
					groupTime = 0;
					errorTime--;
					if (errorTime < 0)
						errorTime = 0;
				}
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	private void publish() throws Exception {
		List<Article> articles = articleFactory.generete();
		for (Article article : articles) {
			logger.debug("get article:" + article.getSubject());
			String editor = article.getEditor();
			boolean isAdministrator = editor.equals(administrator);
			if (!isAdministrator) {
				if (!allowedEditors.contains(editor)) {
					logger.error("Not allowed user " + editor);
					continue;
				}
			}
			articlePublisher.pulish(article, isAdministrator);
		}
	}
}
