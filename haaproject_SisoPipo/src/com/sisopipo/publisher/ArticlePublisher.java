/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 7, 2010 1:06:36 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 7, 2010 1:06:36 AM
 *
 */
package com.sisopipo.publisher;

import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public interface ArticlePublisher {

	public void pulisher(Article article) throws Exception;
}
