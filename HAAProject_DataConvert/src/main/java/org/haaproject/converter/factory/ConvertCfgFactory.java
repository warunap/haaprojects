/**
 * $Revision: 1.10 $
 * $Author: geln_yang $
 * $Date: 2011/08/31 18:05:15 $
 *
 * Author: Eric Yang
 * Date  : Jul 25, 2009 11:15:04 AM
 *
 */
package org.haaproject.converter.factory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ognl.OgnlException;

import org.apache.commons.lang.StringUtils;
import org.haaproject.converter.dom.Component;
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

	private static final String DTD_FILE_PATH = "/cwconverter.dtd";

	private static final String _NODE_COMPONENT = "component";

	private static final String _NODE_LINE = "line";

	private static final String _NODE_Property = "property";

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
			converters.put(converter.getName(), converter);
			pathConverters.put(confPath, converter);
			return converter;
		} catch (Exception e) {
			throw new CfgException(e);
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
		parseAttribute(element, "charset", converter);
		String batched = element.getAttribute("batched");
		if (StringUtils.isNotBlank(batched)) {
			converter.setBatched(Boolean.valueOf(batched));
		}

		String hasLineFlag = element.getAttribute("hasLineFlag");
		if (StringUtils.isNotBlank(hasLineFlag)) {
			converter.setHasLineFlag(Boolean.valueOf(hasLineFlag));
		}

		String lineSize = element.getAttribute("lineSize");
		if (StringUtils.isNotBlank(lineSize)) {
			converter.setLineSize(Integer.parseInt(lineSize));
		}

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
		parseAttribute(element, "name", component);
		parseAttribute(element, "className", component);

		String occurs = element.getAttribute("occurs");
		if (StringUtils.isNotBlank(occurs)) {
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
		if (StringUtils.isNotBlank(trans)) {
			line.setTrans(Boolean.valueOf(trans));
		}

		String length = element.getAttribute("length");
		if (StringUtils.isNotBlank(length))
			line.setLength(Integer.valueOf(length));

		String fixed = element.getAttribute("fixed");
		if (StringUtils.isNotBlank(fixed))
			line.setFixed(Boolean.valueOf(fixed));

		String hasStartKey = element.getAttribute("hasStartKey");
		if (StringUtils.isNotBlank(hasStartKey))
			line.setHasStartKey(Boolean.valueOf(hasStartKey));

		String occurs = element.getAttribute("occurs");
		if (StringUtils.isNotBlank(occurs)) {
			if (occurs.length() != 1 || "1?+*".indexOf(occurs) == -1) {
				throw new CfgException("showType must equal to '1',or '?',or '+',or '*'!");
			}
			line.setOccurs(occurs);
		}

		String needTrans = element.getAttribute("trans");
		if (StringUtils.isNotBlank(needTrans)) {
			line.setTrans(Boolean.valueOf(needTrans));
		} else {
			line.setTrans(false);
		}

		String ignoreMore = element.getAttribute("ignoreMore");
		if (StringUtils.isNotBlank(ignoreMore)) {
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
		if (StringUtils.isNotBlank(align))
			field.setAlign(align);

		String start = element.getAttribute("start");
		if (StringUtils.isNotBlank(start))
			field.setStart(Integer.valueOf(start));

		String end = element.getAttribute("end");
		if (StringUtils.isNotBlank(end))
			field.setEnd(Integer.valueOf(end));

		String index = element.getAttribute("index");
		if (StringUtils.isNotBlank(index))
			field.setIndex(Integer.valueOf(index));

		String length = element.getAttribute("length");
		if (StringUtils.isNotBlank(length))
			field.setLength(Integer.valueOf(length));

		String maxLength = element.getAttribute("maxLength");
		if (StringUtils.isNotBlank(maxLength))
			field.setMaxLength(Integer.valueOf(maxLength));

		String pattern = element.getAttribute("pattern");
		if (StringUtils.isNotBlank(pattern))
			field.setPattern(pattern);

		String precision = element.getAttribute("precision");
		if (StringUtils.isNotBlank(precision))
			field.setPrecision(Integer.valueOf(precision));

		String scale = element.getAttribute("scale");
		if (StringUtils.isNotBlank(scale))
			field.setScale(Integer.valueOf(scale));

		String needFillZero = element.getAttribute("needFillZero");
		if (StringUtils.isNotBlank(needFillZero))
			field.setNeedFillZero(Boolean.valueOf(needFillZero));

		String needRadixPoint = element.getAttribute("needRadixPoint");
		if (StringUtils.isNotBlank(needRadixPoint))
			field.setNeedRadixPoint(Boolean.valueOf(needRadixPoint));

		String format = element.getAttribute("format");
		if (StringUtils.isNotBlank(format))
			field.setFormat(format);

		String optional = element.getAttribute("optional");
		if (StringUtils.isNotBlank(optional))
			field.setOptional(Boolean.valueOf(optional));

		String trim = element.getAttribute("trim");
		if (StringUtils.isNotBlank(optional))
			field.setTrim(Boolean.valueOf(trim));

		String defValue = element.getAttribute("defValue");
		if (StringUtils.isNotBlank(defValue))
			field.setDefValue(defValue);

		String desc = element.getAttribute("desc");
		if (StringUtils.isNotBlank(desc))
			field.setDesc(desc);

		return field;

	}

	private static void parseAttribute(Element element, String name, Object obj) throws OgnlException {
		String value = element.getAttribute(name);
		OgnlUtil.setValue(name, obj, value);
	}

}
