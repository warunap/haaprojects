/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 6, 2010 10:36:10 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 6, 2010 10:36:10 PM
 *
 */
package com.sisopipo.factory;

import java.util.List;
import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public interface ArticleFactory {

	public List<Article> generete() throws Exception;
}
