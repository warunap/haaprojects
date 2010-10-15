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
public class TestDirectoryArticlePublisher extends TestCase {

	DirectoryArticlePublisher publisher = new DirectoryArticlePublisher();

	@Override
	protected void setUp() throws Exception {
		publisher.setBaseDir("d:/test");
	}

	public void testPublish() throws Exception {
		Article article = new Article();
		article.setSubject("hello");
		article.setTags("Science");
		article.setContent("hello my girl");
		article.setEditor("gelnyang@yeah.net");
		article.setDate(new Date());
		publisher.pulish(article, false);
	}

	public void testPublish1() throws Exception {
		Article article = new Article();
		article.setSubject("hello girl");
		article.setTags("girl");
		article.setContent("hello my girl");
		article.setEditor("gelnyang@yeah.net");
		article.setDate(new Date());
		publisher.pulish(article, false);
	}

	public void testPublish2() throws Exception {
		Article article = new Article();
		article.setSubject("10亿用户：Twitter挑战Facebook路还长？");
		article.setTags("Twitter,Facebook,Web");
		article
				.setContent("进入到今年，随着Facebook及Twitter为代表的SNS的飞速发展，二者目前都已经开始试水营收。而Twitter联合创始人埃文?威廉姆斯(Evan Williams)近日表示，作为全球第三大社交网络平台，Twitter的目标是赢得10亿用户，从而与Facebook争夺更多广告主。看来尽管两家的用户营销区别很大，但作为同是全球SNS三甲之列的社交网站，竞争仍在所难免。那么未来Twitter真的可以挑战Facebook，即便是到了10亿用户的规模？威廉姆斯为何刻意强调10亿用户的规模呢？");
		article.setDate(new Date());
		article.setEditor("gelnyang@163.net");
		publisher.pulish(article, false);
	}
}
