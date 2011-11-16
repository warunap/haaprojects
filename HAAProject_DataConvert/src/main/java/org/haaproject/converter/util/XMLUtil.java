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
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.haaproject.converter.dom.Component;
import org.haaproject.converter.dom.Container;
import org.haaproject.converter.dom.Line;
import org.haaproject.converter.dom.Property;
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

	public static StringBuffer component2schema(Component component) {
		StringBuffer buffer = new StringBuffer();
		String schema = component.getNamespace();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
		buffer.append(" xmlns=\"" + schema + "\"");
		buffer.append(" targetNamespace=\"" + schema + "\"");
		buffer.append(" elementFormDefault=\"qualified\"");
		buffer.append(" attributeFormDefault=\"unqualified\">");
		buffer.append(createSchemaElement("xs:", component));
		buffer.append("</xs:schema>");
		return buffer;
	}

	private static StringBuffer createSchemaElement(String nsPrefix, Component component) {
		List<Container> children = component.getChildren();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<" + nsPrefix + "element name=\"" + component.getName() + "\">");
		buffer.append("<" + nsPrefix + "complexType>");
		buffer.append("<" + nsPrefix + "sequence>");
		buffer.append(createSchemaRef(nsPrefix, children));
		buffer.append("</" + nsPrefix + "sequence>");
		buffer.append("</" + nsPrefix + "complexType>");
		buffer.append("</" + nsPrefix + "element>");
		if (children != null && children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				Container child = children.get(i);
				if (child instanceof Line) {
					Line line = (Line) child;
					buffer.append(createSchemaLine(nsPrefix, line));
				} else if (child instanceof Component) {
					buffer.append(createSchemaElement(nsPrefix, (Component) child));
				}
			}
		}
		return buffer;
	}

	private static StringBuffer createSchemaRef(String nsPrefix, List<Container> children) {
		StringBuffer buffer = new StringBuffer();
		if (children != null && children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				Container child = children.get(i);
				buffer.append("<" + nsPrefix + "element ref=\"" + child.getName() + "\" ");
				if (child.isShowNoneOnce() || child.isShowNoneMany()) {
					buffer.append(" minOccurs=\"0\" ");
				}
				if (child.isShowMany() || child.isShowNoneMany()) {
					buffer.append(" maxOccurs=\"unbounded\" ");
				}
				buffer.append(" />");
			}
		}
		return buffer;
	}

	private static Object createSchemaLine(String nsPrefix, Line line) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<" + nsPrefix + "element name=\"" + line.getName() + "\">");
		buffer.append("<" + nsPrefix + "complexType>");
		buffer.append("<" + nsPrefix + "sequence>");
		buffer.append(createSchemaProperties(nsPrefix, line.getProperties()));
		buffer.append("</" + nsPrefix + "sequence>");
		buffer.append("</" + nsPrefix + "complexType>");
		buffer.append("</" + nsPrefix + "element>");
		return buffer;
	}

	private static Object createSchemaProperties(String nsPrefix, List<Property> properties) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < properties.size(); i++) {
			Property property = properties.get(i);
			String type = property.getType();
			String schemaType = "string";
			if (Property.TYPE_INTEGER.equals(type))
				schemaType = "int";
			if (Property.TYPE_LONG.equals(type))
				schemaType = "long";
			if (Property.TYPE_BIGDECIMAL.equals(type))
				schemaType = "decimal";
			if (Property.TYPE_DATE.equals(type))
				schemaType = "date";

			buffer.append("<" + nsPrefix + "element name=\"" + property.getName() + "\" type=\"" + nsPrefix
					+ schemaType + "\"");
			if (property.isOptional()) {
				buffer.append(" minOccurs=\"0\" ");
			}
			buffer.append("/>");
		}

		return buffer;
	}

}
