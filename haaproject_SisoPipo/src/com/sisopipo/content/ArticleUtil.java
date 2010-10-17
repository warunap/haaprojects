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

import java.text.ParseException;
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
import c4j.xml.XmlUtil;
import com.sisopipo.ArticleContext;
import com.sisopipo.exception.OperatonException;

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

	public static Document addItem(Document document, Article article) throws DocumentException, OperatonException {
		Element root = document.getRootElement();

		String rootNameSpaceURI = root.getNamespace().getURI();
		Map<String, String> xmlns = new HashMap<String, String>();
		xmlns.put("ns", rootNameSpaceURI);
		String xpath = "//ns:list/ns:item/ns:subject[text()='" + article.getSubject() + "']";
		List<Element> nodes = XmlUtil.parseNodes(xpath, root, xmlns);

		if (nodes != null && nodes.size() > 0)
			throw new OperatonException("Already exists article:" + article.getSubject());

		Element itemElement = createArticleItem(article);

		root.add(itemElement);

		return document;
	}

	private static Element createArticleItem(Article article) {
		Element itemElement = new DOMElement("item");
		itemElement.add(createKeyValueElement("subject", article.getSubject()));
		itemElement.add(createKeyValueElement("editor", article.getEditor()));
		itemElement.add(createKeyValueElement("tags", article.getTags()));
		itemElement.add(createKeyValueElement("date", new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(article.getDate())));
		itemElement.add(createKeyValueElement("path", article.getPath()));
		return itemElement;
	}

	private static Element createKeyValueElement(String name, String value) {
		Element element = new DOMElement(name);
		Text text = new DOMText(value);
		element.add(text);
		return element;
	}

	@SuppressWarnings("unchecked")
	public static String removeItem(Document document, Article article) throws Exception {
		Element targetElement = getArticleFromDocument(document, article.getSubject());

		if (targetElement != null) {
			Article storedArticle = parse(targetElement);
			if (!ArticleContext.getAdministrator().equals(article.getEditor())) {
				String editor = storedArticle.getEditor();
				if (!editor.equalsIgnoreCase(article.getEditor())) {
					throw new OperatonException("Article[" + article.getSubject() + "] published by " + editor + " can't be removed by " + article.getEditor());
				}
			}
			document.getRootElement().remove(targetElement);
			return storedArticle.getPath();
		} else {
			logger.warn("No article[" + article.getSubject() + "]!");
			return null;
		}
	}

	private static Element getArticleFromDocument(Document document, String subject) throws ParseException {
		Element root = document.getRootElement();
		String rootNameSpaceURI = root.getNamespace().getURI();
		Map<String, String> xmlns = new HashMap<String, String>();
		xmlns.put("ns", rootNameSpaceURI);
		String xpath = "//ns:list/ns:item/ns:subject[text()='" + subject + "']";
		List<Element> nodes = XmlUtil.parseNodes(xpath, root, xmlns);

		Element targetElement = null;
		if (nodes != null && nodes.size() > 0) {
			return nodes.get(0).getParent();
		}

		return null;
	}

	public static Article parse(Element artileElementItem) throws ParseException {
		Article article = new Article();
		List content = artileElementItem.content();
		for (Object e : content) {
			if (e instanceof Element) {
				Element item = (Element) e;
				if ("editor".equals(item.getName())) {
					article.setEditor(item.getTextTrim());
				} else if ("subject".equals(item.getName())) {
					article.setTags(item.getTextTrim());
				} else if ("tags".equals(item.getName())) {
					article.setTags(item.getTextTrim());
				} else if ("date".equals(item.getName())) {
					article.setDate(new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(item.getTextTrim()));
				} else if ("path".equals(item.getName())) {
					article.setPath(item.getTextTrim());
				}
			}
		}
		return article;
	}

}
