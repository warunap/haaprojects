/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 7, 2010 1:49:22 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 7, 2010 1:49:22 PM
 *
 */
package com.sisopipo.content;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;
import c4j.xml.XmlUtil;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ArticleUtil {

	private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	public static String createListFileContent() {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		s += "<list>";
		s += "</list>";
		return s;
	}

	public static Document createListDocument() throws DocumentException {
		return getDocument(createListFileContent());
	}

	public static Document getDocument(String content) throws DocumentException {
		return XmlUtil.parse(content);
	}

	public static void addItem(Document document, Article article) throws DocumentException {
		addItem(document, article.getSubject(), article.getCategory(), article.getDate());
	}

	public static Document addItem(Document document, String subject, String category, Date date) throws DocumentException {
		Element itemElement = new DOMElement("item");
		itemElement.add(createKeyValueElement("subject", subject));
		itemElement.add(createKeyValueElement("category", category));
		itemElement.add(createKeyValueElement("date", new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date)));

		Element root = document.getRootElement();
		root.add(itemElement);

		return document;
	}

	private static Element createKeyValueElement(String name, String value) {
		Element element = new DOMElement(name);
		Text text = new DOMText(value);
		element.add(text);
		return element;
	}

}
