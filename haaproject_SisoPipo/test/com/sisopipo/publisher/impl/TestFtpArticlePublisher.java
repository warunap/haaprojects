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

import java.util.Date;
import junit.framework.TestCase;
import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestFtpArticlePublisher extends TestCase {

	FtpArticlePublisher publisher = new FtpArticlePublisher();

	protected void setUp() throws Exception {
		publisher.setBaseDir("/logs/");
		publisher.setFtpurl("ftp://user.ftp.com/");
		publisher.setFtpport(21);
		publisher.setFtpuser("user");
		publisher.setFtppasswd("*****");
	}

	public void testPublish() throws Exception {
		Article article = new Article();
		article.setSubject("hello");
		article.setTags("Science");
		article.setContent("hello my girl");
		article.setDate(new Date());
		publisher.pulish(article, false);
		System.out.println("over");
	}
}
