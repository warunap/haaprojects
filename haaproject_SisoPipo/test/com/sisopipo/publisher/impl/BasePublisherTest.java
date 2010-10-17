/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 17, 2010 11:56:04 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 17, 2010 11:56:04 AM
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
public class BasePublisherTest extends TestCase {

	protected Article newArticle(String subject, String content, String tags, String editor) {
		Article article = new Article();
		article.setSubject(subject);
		article.setTags(tags);
		article.setContent(content);
		article.setDate(new Date());
		article.setEditor(editor);
		return article;
	}
}
