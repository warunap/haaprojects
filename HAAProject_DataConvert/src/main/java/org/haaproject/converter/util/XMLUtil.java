/**
 * $Revision: 1.3 $
 * $Author: vincent_huang $
 * $Date: 2011/03/25 10:33:21 $
 *
 * Author: Eric Yang
 * Date  : Jun 2, 2010 9:56:02 AM
 *
 */
package org.haaproject.converter.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @Author Eric Yang
 * @version 1.0
 */
public final class XMLUtil {

	static TransformerFactory factory = TransformerFactory.newInstance();

	public static String transform(String xslt, String xml, String charsetName) throws TransformerException {
		return transform(new StreamSource(new StringReader(xslt)), new StreamSource(new StringReader(xml)), charsetName);
	}

	public static String transform(InputStream xsltStream, InputStream xmlStream, String charset)
			throws TransformerException {
		return transform(new StreamSource(xsltStream), new StreamSource(xmlStream), charset);
	}

	public static String transform(Source xsltStream, Source xmlStream, String charset) throws TransformerException {
		try {

			Transformer transformer = factory.newTransformer(xsltStream);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			transformer.transform(xmlStream, new StreamResult(outputStream));
			transformer = null;
			xmlStream = null;
			return outputStream.toString(charset);
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

	public static Element getFirstElementChild(Element element) {
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) node;
			}
		}
		return null;
	}

}
