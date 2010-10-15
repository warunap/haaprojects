/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 16, 2010 12:15:33 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 16, 2010 12:15:33 AM
 *
 */
package com.sisopipo.content;

import java.io.IOException;
import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import com.sisopipo.exception.OperatonException;
import c4j.io.StreamUtil;
import c4j.xml.XmlUtil;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestArticleUtil extends TestCase {

	public void testRemove() throws IOException, DocumentException, OperatonException {
		String s = StreamUtil.readConfigureToString("list.xml");
		System.out.println(s);
		System.out.println("===========================================");
		Document document = XmlUtil.parse(s);

		Article article = new Article();
		article.setSubject("test remove subject");
		try {
			article.setEditor("gelnyang@aa.com");
			ArticleUtil.removeItem(document, article, false);
			fail("Should not allowed!");
		} catch (Exception e) {
		}
		article.setEditor("gelnyang@163.com");
		ArticleUtil.removeItem(document, article, false);
		System.out.println(document.asXML());
	}
}
