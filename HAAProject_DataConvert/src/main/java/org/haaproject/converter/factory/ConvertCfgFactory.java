/**
 * $Revision: 1.10 $ $Author: geln_yang $ $Date: 2011/08/31 18:05:15 $
 * 
 * Author: Eric Yang Date : Jul 25, 2009 11:15:04 AM
 * 
 */
package org.haaproject.converter.factory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ognl.OgnlException;

import org.apache.commons.lang.StringUtils;
import org.haaproject.converter.dom.Component;
import org.haaproject.converter.dom.Container;
import org.haaproject.converter.dom.Converter;
import org.haaproject.converter.dom.Line;
import org.haaproject.converter.dom.Property;
import org.haaproject.converter.exception.CfgException;
import org.haaproject.converter.util.OgnlUtil;
import org.haaproject.converter.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * cw document definition configuration Factory
 * 
 * @author Eric Yang
 * @version 1.0
 */
public class ConvertCfgFactory {

	private static final String DTD_FILE_PATH = "/haaconverter.dtd";

	private static final String _NODE_COMPONENT = "component";

	private static final String _NODE_LINE = "line";

	private static final String _NODE_Property = "property";

	private static final Object NOTSET_VAL = "_NOSET_VAL_";

	private static Map<String, Converter> converters = new HashMap<String, Converter>();

	private static Map<String, Converter> pathConverters = new HashMap<String, Converter>();

	public static Converter getConverter(String key) {
		return converters.get(key);
	}

	public static Converter load(String confPath) throws CfgException {
		Converter comp = pathConverters.get(confPath);
		if (comp != null)
			return comp;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			if (DTD_FILE_PATH != null) {
				builder.setEntityResolver(new EntityResolver() {
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
						InputStream dtd_stream = ConvertCfgFactory.class.getResourceAsStream(DTD_FILE_PATH);
						return new InputSource(dtd_stream);
					}
				});
			}
			Document document;

			InputStream is = ConvertCfgFactory.class.getResourceAsStream(confPath);

			/* if can't find configuration file under classpath, then find it by file path. */
			if (is == null) {
				is = new FileInputStream(confPath);
			}
			document = builder.parse(is);

			Element docElement = document.getDocumentElement();

			Converter converter = parseConverter(docElement);

			setContainerNext(converter.getComponent());

			converters.put(converter.getName(), converter);
			pathConverters.put(confPath, converter);
			return converter;
		} catch (Exception e) {
			throw new CfgException(e);
		}
	}

	/**
	 * Set the next object for the same level.The next object of the last one is null in the same level.
	 */
	private static void setContainerNext(Component component) {
		List<Container> children = component.getChildren();
		if (children.size() > 1) {
			for (int i = 0; i < children.size() - 1; i++) {
				Container c1 = children.get(i);
				Container c2 = children.get(i + 1);
				c1.setNext(c2);
			}

			for (int i = 0; i < children.size(); i++) {
				Container c = children.get(i);
				if (c instanceof Component) {
					setContainerNext((Component) c);
				}
			}
		}

	}

	public static Converter loadFromContent(String content) throws CfgException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			if (DTD_FILE_PATH != null) {
				builder.setEntityResolver(new EntityResolver() {
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
						InputStream dtd_stream = this.getClass().getResourceAsStream(DTD_FILE_PATH);
						return new InputSource(dtd_stream);
					}
				});
			}
			Document document;

			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			document = builder.parse(is);

			Element docElement = document.getDocumentElement();

			Converter converter = parseConverter(docElement);
			converters.put(converter.getName(), converter);
			return converter;
		} catch (Exception e) {
			throw new CfgException(e);
		}
	}

	private static Converter parseConverter(Element element) throws OgnlException, CfgException {
		Converter converter = new Converter();
		parseAttribute(element, "charset", converter, Converter.DEFAULT_CHARSET);
		parseAttribute(element, "batchSize", converter, Converter.DEFAULT_BATCH_SIZE);
		parseAttribute(element, "batched", converter, false);
		parseAttribute(element, "hasLineFlag", converter, false);
		parseOptionalAttribute(element, "lineSize", converter);

		if (!converter.isHasLineFlag() && converter.getLineSize() <= 0) {
			throw new CfgException("component line size must be great than 0!");
		}

		element = XMLUtil.getFirstElementChild(element); // get root Component
		Component component = parseComponent(element);
		component.setConverter(converter);
		converter.setComponent(component);
		return converter;
	}

	private static Component parseComponent(Element element) throws OgnlException, CfgException {
		Component component = new Component();
		parseAttribute(element, "name", component, null);
		parseAttribute(element, "className", component, null);

		String occurs = element.getAttribute("occurs");
		if (StringUtils.isNotBlank(occurs)) {
			if (!(Container.OCCURS_MANY.equals(occurs) || Container.OCCURS_NONE_MANY.equals(occurs)
					|| Container.OCCURS_ONCE.equals(occurs) || Container.OCCURS_NONE_ONCE.equals(occurs))) {
				throw new CfgException("Wrong showType!");
			}
			component.setOccurs(occurs);
		}

		NodeList childNodes = element.getChildNodes();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0, nodeSize = childNodes.getLength(); i < nodeSize; i++) {
				Node childElement = childNodes.item(i);
				if (childElement.getNodeType() == Node.ELEMENT_NODE) {
					if (_NODE_COMPONENT.equals(childElement.getNodeName())) {
						Component child = parseComponent((Element) childElement);
						component.addComponent(child);
					} else if (_NODE_LINE.equals(childElement.getNodeName())) {
						Line line = parseLine((Element) childElement, component);
						component.addComponent(line);
					}
				}
			}
		} else {
			throw new CfgException("no child for node " + element.getNodeName());
		}
		return component;
	}

	private static Line parseLine(Element element, Component component) throws CfgException, OgnlException {
		Line line = new Line();
		parseAttribute(element, "name", line);
		parseAttribute(element, "className", line);
		parseAttribute(element, "trans", line, false);
		parseOptionalAttribute(element, "startKey", line);
		parseOptionalAttribute(element, "split", line);
		parseOptionalAttribute(element, "length", line);
		parseOptionalAttribute(element, "fixed", line);
		parseOptionalAttribute(element, "hasStartKey", line);
		parseAttribute(element, "trans", line, false);
		parseAttribute(element, "ignoreMore", line, false);

		String occurs = element.getAttribute("occurs");
		if (StringUtils.isNotBlank(occurs)) {
			if (!(Container.OCCURS_MANY.equals(occurs) || Container.OCCURS_NONE_MANY.equals(occurs)
					|| Container.OCCURS_ONCE.equals(occurs) || Container.OCCURS_NONE_ONCE.equals(occurs))) {
				throw new CfgException("Wrong showType!");
			}
			line.setOccurs(occurs);
		}

		NodeList childNodes = element.getChildNodes();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0, nodeSize = childNodes.getLength(); i < nodeSize; i++) {
				Node node = childNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE && _NODE_Property.equals(node.getNodeName())) {
					Element child = (Element) node;
					Property field = parseFields(child);
					line.addField(field);
				}
			}
		}

		return line;
	}

	private static Property parseFields(Element element) throws OgnlException, CfgException {
		Property field = new Property();

		parseAttribute(element, "name", field);
		parseAttribute(element, "type", field, Property.TYPE_STRING);
		parseAttribute(element, "align", field, Property.LEFT);

		parseOptionalAttribute(element, "start", field);
		parseOptionalAttribute(element, "end", field);
		parseOptionalAttribute(element, "index", field);
		parseOptionalAttribute(element, "length", field);
		parseOptionalAttribute(element, "maxLength", field);
		parseOptionalAttribute(element, "pattern", field);
		parseOptionalAttribute(element, "precision", field);
		parseOptionalAttribute(element, "scale", field);
		parseOptionalAttribute(element, "needFillZero", field);
		parseOptionalAttribute(element, "needRadixPoint", field);
		parseOptionalAttribute(element, "format", field);
		parseOptionalAttribute(element, "optional", field);
		parseOptionalAttribute(element, "trim", field);
		parseOptionalAttribute(element, "defValue", field);
		parseOptionalAttribute(element, "desc", field);

		return field;
	}

	private static void parseAttribute(Element element, String name, Object obj) throws OgnlException, CfgException {
		parseAttribute(element, name, obj, null);
	}

	private static void parseOptionalAttribute(Element element, String name, Object obj) throws OgnlException,
			CfgException {
		parseAttribute(element, name, obj, NOTSET_VAL);
	}

	private static void parseAttribute(Element element, String name, Object obj, Object defautlVal)
			throws OgnlException, CfgException {
		String value = element.getAttribute(name);
		if (StringUtils.isEmpty(value)) {
			if (defautlVal == null) {
				throw new CfgException("No attribute " + name);
			}
			if (NOTSET_VAL.equals(defautlVal.toString())) {
				return;
			}
			value = defautlVal.toString();
		}
		if ("true".equals(value) || "false".equals(value)) {
			OgnlUtil.setValue(name, obj, Boolean.valueOf(value));
		} else {
			OgnlUtil.setValue(name, obj, value);
		}
	}
}
