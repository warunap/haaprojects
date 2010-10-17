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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sisopipo.ArticleContext;
import com.sisopipo.content.Article;
import com.sisopipo.factory.ArticleFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class PublishThread extends Thread {

	private static final Log logger = LogFactory.getLog(PublishThread.class);

	public void run() {
		if (ArticleContext.getArticlePublisher() == null) {
			logger.error("No article publisher setting!");
			return;
		}

		if (ArticleContext.getArticleFactory() == null) {
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
				Thread.sleep((ArticleContext.getInterval() + errorTime) * 1000);

				/* reduce error times in a group time */
				if (groupTime >= 10) {
					groupTime = 0;
					errorTime--;
					if (errorTime < 0)
						errorTime = 0;
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void publish() throws Exception {
		ArticleFactory articleFactory = ArticleContext.getArticleFactory();
		List<Article> articles = articleFactory.generete();
		if (articles != null && articles.size() > 0) {
			ArticlePublisher articlePublisher = ArticleContext.getArticlePublisher();
			articlePublisher.publish(articles);
		}
	}
}
