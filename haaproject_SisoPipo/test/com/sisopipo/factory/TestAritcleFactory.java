/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 7, 2010 12:42:31 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 7, 2010 12:42:31 AM
 *
 */
package com.sisopipo.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sisopipo.content.Article;
import com.sisopipo.factory.ArticleFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestAritcleFactory implements ArticleFactory {

	public static final Log logger = LogFactory.getLog(TestAritcleFactory.class);

	public List<Article> generete() throws Exception {
		List<Article> articles = new ArrayList<Article>();
		Article article = newArticle("hi 163", "163 good", "163", "gelnyang@gmail.com");
		articles.add(article);
		article = newArticle("hello baidu", "baidu good", "baidu", "gelnyang@gmail.com");
		articles.add(article);
		article = newArticle("hello sina", "sina good", "sina", "gelnyang@gmail.com");
		articles.add(article);
		article = newArticle("你知道的", "我知道", "sina", "gelnyang@163.net");
		articles.add(article);
		article = newArticle(" 有什么不知道", "我知道", "sina", "gelnyang@163.net");
		articles.add(article);
		return articles;
	}

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
