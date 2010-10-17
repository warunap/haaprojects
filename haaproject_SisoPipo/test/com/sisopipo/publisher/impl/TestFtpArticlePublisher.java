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
import java.util.Properties;
import c4j.file.FileUtil;
import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestFtpArticlePublisher extends BasePublisherTest {

	FtpArticlePublisher publisher = new FtpArticlePublisher();

	protected void setUp() throws Exception {
		Properties properties = FileUtil.readFileAsProperties("sisopipo.properties");
		publisher.setLocalBaseDir(properties.getProperty("publisher.local.basedir"));
		publisher.setFtpBaseDir(properties.getProperty("publisher.ftp.basedir"));
		publisher.setFtpurl(properties.getProperty("publisher.ftp.url"));
		publisher.setFtpport(21);
		publisher.setFtpuser(properties.getProperty("publisher.ftp.user"));
		publisher.setFtppasswd(properties.getProperty("publisher.ftp.password"));
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

		publisher.finishPublish();
		System.out.println("over");
	}

}
