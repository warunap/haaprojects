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

import java.io.IOException;
import java.util.List;
import com.sisopipo.content.Article;

/**
 * @author Geln Yang
 * @version 1.0
 */
public interface ArticlePublisher {

	public void publish(List<Article> articles) throws Exception;

	public void finishPublish() throws IOException;

}
