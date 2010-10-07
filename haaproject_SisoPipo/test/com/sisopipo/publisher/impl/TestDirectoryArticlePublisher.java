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
import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestDirectoryArticlePublisher {

	public static void main(String[] args) throws Exception {
		DirectoryArticlePublisher publisher = new DirectoryArticlePublisher();
		publisher.setBaseDir("d:/test");
		Article article = new Article();
		article.setSubject("hello");
		article.setCategory("Science");
		article.setContent("hello my girl");
		article.setDate(new Date());
		publisher.pulisher(article);
	}
}
