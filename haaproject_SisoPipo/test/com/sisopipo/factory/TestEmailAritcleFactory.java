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

import java.util.List;
import com.sisopipo.content.Article;
import com.sisopipo.factory.impl.EmailAritcleFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestEmailAritcleFactory {

	public static void main(String[] args) throws Exception {
		List<Article> articles = new EmailAritcleFactory().generete();
		for (Article article : articles) {
			System.out.println(article.getSubject());
			System.out.println(article.getDate());
			System.out.println(article.getTags());
			System.out.println(article.getContent());
		}
		System.out.println("over");
	}

}
