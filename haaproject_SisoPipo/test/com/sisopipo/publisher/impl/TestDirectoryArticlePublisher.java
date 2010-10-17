/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 7, 2010 2:28:20 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 7, 2010 2:28:20 PM
 *
 */
package com.sisopipo.publisher.impl;

import java.util.ArrayList;
import java.util.List;
import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestDirectoryArticlePublisher extends BasePublisherTest {

	DirectoryArticlePublisher publisher = new DirectoryArticlePublisher();

	@Override
	protected void setUp() throws Exception {
		publisher.setLocalBaseDir("d:/test");
	}

	public void testPublish() throws Exception {
		List<Article> articles = new ArrayList<Article>();
		Article article = newArticle("hi 163", "163 good", "163", "gelnyang@gmail.com");
		articles.add(article);
		article = newArticle("hello baidu", "baidu good", "baidu", "gelnyang@gmail.com");
		articles.add(article);
		article = newArticle("hello sina", "sina good", "sina", "gelnyang@gmail.com");
		articles.add(article);

		publisher.publish(articles);
	}

	public void testRemove() throws Exception {
		List<Article> articles = new ArrayList<Article>();
		Article article = newArticle("hello baidu", "baidu good", "baidu", "gelnyang@gmail.com");
		article.setAction("remove");
		articles.add(article);
		publisher.publish(articles);
	}
}
