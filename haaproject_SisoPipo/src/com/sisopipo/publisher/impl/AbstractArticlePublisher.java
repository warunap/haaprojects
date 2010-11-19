/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 17, 2010 11:29:39 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 17, 2010 11:29:39 AM
 *
 */
package com.sisopipo.publisher.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sisopipo.ArticleContext;
import com.sisopipo.content.Article;
import com.sisopipo.publisher.ArticlePublisher;

/**
 * @author Geln Yang
 * @version 1.0
 */
public abstract class AbstractArticlePublisher implements ArticlePublisher {

	private static final Log logger = LogFactory.getLog(AbstractArticlePublisher.class);

	public final void publish(List<Article> articles) throws Exception {
		if (articles == null || articles.size() == 0)
			return;

		List<Article> removeArticles = new ArrayList<Article>();
		List<Article> addArticles = new ArrayList<Article>();

		for (Article article : articles) {
			logger.debug("get article:" + article.getSubject());
			String editor = article.getEditor();

			if (article.toRemove())
				removeArticles.add(article);

			if (article.toAdd()) {
				if (!ArticleContext.editorLimited() || editor.equals(ArticleContext.getAdministrator()) || ArticleContext.isAuthenticateEditor(editor))
					addArticles.add(article);
				else {
					logger.error("Not allowed publish!" + article.getEditor() + "," + article.getSubject());
				}
			}
		}
		/* remove articles */
		for (Article article : removeArticles) {
			try {
				removeArticle(article);
			} catch (Exception e) {
				logger.error(e);
			}
		}

		/* add new articles */
		for (Article article : addArticles) {
			try {
				publishArticle(article);
			} catch (Exception e) {
				logger.error(e);
			}
		}

		/* close connection */
		finishPublish();
	}

	protected abstract void publishArticle(Article article) throws Exception;

	protected abstract void removeArticle(Article article) throws Exception;

}
