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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;
import com.sisopipo.exception.OperatonException;
import c4j.xml.XmlUtil;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ArticleUtil {
	public static final Log logger = LogFactory.getLog(ArticleUtil.class);

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

	public static Document addItem(Document document, Article article) throws DocumentException {
		Element itemElement = createArticleItem(article);

		Element root = document.getRootElement();
		root.add(itemElement);

		return document;
	}

	private static Element createArticleItem(Article article) {
		Element itemElement = new DOMElement("item");
		itemElement.add(createKeyValueElement("subject", article.getSubject()));
		itemElement.add(createKeyValueElement("editor", article.getEditor()));
		itemElement.add(createKeyValueElement("tags", article.getTags()));
		itemElement.add(createKeyValueElement("date", new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(article.getDate())));
		return itemElement;
	}

	private static Element createKeyValueElement(String name, String value) {
		Element element = new DOMElement(name);
		Text text = new DOMText(value);
		element.add(text);
		return element;
	}

	@SuppressWarnings("unchecked")
	public static void removeItem(Document document, Article article, boolean enforce) throws OperatonException {
		Element root = document.getRootElement();
		String rootNameSpaceString = root.getNamespace().getURI();
		Map<String, String> xmlns = new HashMap<String, String>();
		xmlns.put("ns", rootNameSpaceString);
		String xpath = "//ns:list/ns:item/ns:subject";
		List<Element> nodes = XmlUtil.parseNodes(xpath, root, xmlns);

		Element targetElement = null;
		if (nodes != null) {
			for (Element element : nodes) {
				String text = element.getText();
				if (article.getSubject().equals(text)) {
					targetElement = element.getParent();
					break;
				}
			}
		}
		if (targetElement != null) {
			if (!enforce) {
				String editor = "";
				List content = targetElement.content();
				for (Object e : content) {
					if (e instanceof Element) {
						Element item = (Element) e;
						if ("editor".equals(item.getName())) {
							editor = item.getTextTrim();
							break;
						}
					}
				}
				if (!editor.equalsIgnoreCase(article.getEditor())) {
					throw new OperatonException("Article[" + article.getSubject() + "] published by " + editor + " can't be removed by " + article.getEditor());
				}
			}
			root.remove(targetElement);
		} else {
			logger.warn("No article[" + article.getSubject() + "]!");
		}
	}

}
