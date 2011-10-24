/**
 * $Revision: 1.10 $
 * $Author: geln_yang $
 * $Date: 2011/08/31 18:05:15 $
 *
 * Author: Eric Yang
 * Date  : Jul 25, 2009 11:15:04 AM
 *
 */
package org.haa.dataconvert.factory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ognl.OgnlException;

import org.haa.dataconvert.dom.Component;
import org.haa.dataconvert.dom.Line;
import org.haa.dataconvert.dom.Property;
import org.haa.dataconvert.exception.CfgException;
import org.haa.dataconvert.util.OgnlUtil;
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

	private static final String dtdFile = "/cwconverter.dtd";

	private static final String _NODE_COMPONENT = "component";

	private static final String _NODE_LINE = "line";

	private static final String _NODE_Property = "property";

	private static Map<String, Component> components = new HashMap<String, Component>();

	private static Map<String, Component> pathComponents = new HashMap<String, Component>();

	public static Component getComponent(String key) {
		return components.get(key);
	}

	public static Component load(String confPath) throws CfgException {
		Component comp = pathComponents.get(confPath);
		if (comp != null)
			return comp;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			if (dtdFile != null) {
				builder.setEntityResolver(new EntityResolver() {
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
						InputStream dtd_stream = this.getClass().getResourceAsStream(dtdFile);
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

			Component component = parseComponent(docElement);
			components.put(component.getName(), component);
			pathComponents.put(confPath, component);
			return component;
		} catch (Exception e) {
			throw new CfgException(e);
		}
	}

	public static Component loadFromContent(String content) throws CfgException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			if (dtdFile != null) {
				builder.setEntityResolver(new EntityResolver() {
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
						InputStream dtd_stream = this.getClass().getResourceAsStream(dtdFile);
						return new InputSource(dtd_stream);
					}
				});
			}
			Document document;

			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			document = builder.parse(is);

			Element docElement = document.getDocumentElement();

			Component component = parseComponent(docElement);
			components.put(component.getName(), component);
			return component;
		} catch (Exception e) {
			throw new CfgException(e);
		}
	}

	private static Component parseComponent(Element element) throws OgnlException, CfgException {
		Component component = new Component();
		parseAttribute(element, "name", component);
		parseAttribute(element, "className", component);
		parseAttribute(element, "charset", component);

		String hasLineFlag = element.getAttribute("hasLineFlag");
		if (isNotNull(hasLineFlag)) {
			component.setHasLineFlag(Boolean.valueOf(hasLineFlag));
		}

		String lineSize = element.getAttribute("lineSize");
		if (isNotNull(lineSize)) {
			component.setLineSize(Integer.parseInt(lineSize));
		}

		if (!component.isHasLineFlag() && component.getLineSize() <= 0) {
			throw new CfgException("component line size must be great than 0!");
		}

		String occurs = element.getAttribute("occurs");
		if (isNotNull(occurs)) {
			if (occurs.length() != 1 || "1?+*".indexOf(occurs) == -1) {
				throw new CfgException("showType must equal to '1',or '?',or '+',or '*'!");
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
		parseAttribute(element, "startKey", line);
		parseAttribute(element, "split", line);
		parseAttribute(element, "trans", line);

		String trans = element.getAttribute("trans");
		if (isNotNull(trans)) {
			line.setTrans(Boolean.valueOf(trans));
		}

		String length = element.getAttribute("length");
		if (isNotNull(length))
			line.setLength(Integer.valueOf(length));

		String fixed = element.getAttribute("fixed");
		if (isNotNull(fixed))
			line.setFixed(Boolean.valueOf(fixed));

		String hasStartKey = element.getAttribute("hasStartKey");
		if (isNotNull(hasStartKey))
			line.setHasStartKey(Boolean.valueOf(hasStartKey));

		String occurs = element.getAttribute("occurs");
		if (isNotNull(occurs)) {
			if (occurs.length() != 1 || "1?+*".indexOf(occurs) == -1) {
				throw new CfgException("showType must equal to '1',or '?',or '+',or '*'!");
			}
			line.setOccurs(occurs);
		}

		String needTrans = element.getAttribute("trans");
		if (isNotNull(needTrans)) {
			line.setTrans(Boolean.valueOf(needTrans));
		} else {
			line.setTrans(false);
		}

		String ignoreMore = element.getAttribute("ignoreMore");
		if (isNotNull(ignoreMore)) {
			line.setIgnoreMore(Boolean.valueOf(ignoreMore));
		} else {
			line.setIgnoreMore(false);
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

	private static Property parseFields(Element element) throws OgnlException {
		Property field = new Property();

		parseAttribute(element, "name", field);
		parseAttribute(element, "desc", field);
		parseAttribute(element, "type", field);

		String align = element.getAttribute("align");
		if (isNotNull(align))
			field.setAlign(align);

		String start = element.getAttribute("start");
		if (isNotNull(start))
			field.setStart(Integer.valueOf(start));

		String end = element.getAttribute("end");
		if (isNotNull(end))
			field.setEnd(Integer.valueOf(end));

		String index = element.getAttribute("index");
		if (isNotNull(index))
			field.setIndex(Integer.valueOf(index));

		String length = element.getAttribute("length");
		if (isNotNull(length))
			field.setLength(Integer.valueOf(length));

		String maxLength = element.getAttribute("maxLength");
		if (isNotNull(maxLength))
			field.setMaxLength(Integer.valueOf(maxLength));

		String pattern = element.getAttribute("pattern");
		if (isNotNull(pattern))
			field.setPattern(pattern);

		String precision = element.getAttribute("precision");
		if (isNotNull(precision))
			field.setPrecision(Integer.valueOf(precision));

		String scale = element.getAttribute("scale");
		if (isNotNull(scale))
			field.setScale(Integer.valueOf(scale));

		String needFillZero = element.getAttribute("needFillZero");
		if (isNotNull(needFillZero))
			field.setNeedFillZero(Boolean.valueOf(needFillZero));

		String needRadixPoint = element.getAttribute("needRadixPoint");
		if (isNotNull(needRadixPoint))
			field.setNeedRadixPoint(Boolean.valueOf(needRadixPoint));

		String format = element.getAttribute("format");
		if (isNotNull(format))
			field.setFormat(format);

		String optional = element.getAttribute("optional");
		if (isNotNull(optional))
			field.setOptional(Boolean.valueOf(optional));

		String defValue = element.getAttribute("defValue");
		if (isNotNull(defValue))
			field.setDefValue(defValue);

		String desc = element.getAttribute("desc");
		if (isNotNull(desc))
			field.setDesc(desc);

		return field;

	}

	private static void parseAttribute(Element element, String name, Object obj) throws OgnlException {
		String value = element.getAttribute(name);
		OgnlUtil.setValue(name, obj, value);
	}

	private static boolean isNotNull(String s) {
		return s != null && s.trim().length() > 0;
	}
}
