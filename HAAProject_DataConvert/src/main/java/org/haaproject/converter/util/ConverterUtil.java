/**
 * $Revision: 1.29 $
 * $Author: geln_yang $
 * $Date: 2011/09/06 12:51:17 $
 *
 * Author: Eric Yang
 * Date  : May 11, 2010 4:11:29 PM
 *
 */
package org.haaproject.converter.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ognl.OgnlException;

import org.haaproject.converter.dom.Component;
import org.haaproject.converter.dom.Container;
import org.haaproject.converter.dom.Line;
import org.haaproject.converter.dom.Property;
import org.haaproject.converter.exception.BuildException;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.reader.BatchReader;
import org.haaproject.converter.reader.IndexReader;
import org.haaproject.converter.reader.IndexStreamReader;
import org.haaproject.converter.reader.ReadStatus;

/**
 * @Author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public final class ConverterUtil implements Serializable {

	public static void validateContent(Component component, String fileContent) throws IOException, ParseException {
		StringReader sReader = new StringReader(fileContent);
		BufferedReader reader = new BufferedReader(sReader);
		validate(component, reader);
	}

	public static void validateFile(Component component, String filePath) throws IOException, ParseException {
		FileInputStream inputStream = new FileInputStream(filePath);
		try {
			validate(component, inputStream);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public static void validate(Component component, InputStream inputStream) throws IOException, ParseException {
		try {
			if (!component.getConverter().isHasLineFlag()) {
				IndexReader indexReader = new IndexStreamReader(inputStream, component);
				if (!validate(component, indexReader))
					throw new ParseException("[line:" + indexReader.lineNum() + "]unexpected line,invalid startKey!");
				return;
			} else {
				InputStreamReader inr = new InputStreamReader(inputStream, component.getConverter().getCharset());
				try {
					BufferedReader reader = new BufferedReader(inr);
					validate(component, reader);
				} finally {
					try {
						inr.close();
					} catch (Exception e) {
					}
				}
			}
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public static void validate(Component component, BufferedReader bufferedReader) throws IOException, ParseException {
		IndexReader indexReader = new IndexReader(bufferedReader, component);
		if (!validate(component, indexReader))
			throw new ParseException("[line:" + indexReader.lineNum() + "]unexpected line,invalid startKey!");
	}

	private static boolean validate(Component component, IndexReader reader) throws IOException, ParseException {
		boolean foundStartKey = onceValidate(component, reader);

		if (component.isShowMany() || component.isShowNoneMany()) {
			String lineContent = reader.popTopLine();
			if (lineContent == null)
				return true;

			/* loop component */
			while (component.isBelongToMe(lineContent)) {
				foundStartKey = onceValidate(component, reader);
				lineContent = reader.popTopLine();
				if (lineContent == null)
					break;
			}
		}
		return foundStartKey;
	}

	private static boolean onceValidate(Component component, IndexReader reader) throws IOException, ParseException {
		List<Container> children = component.getChildren();
		boolean foundStartKey = true;// 最后一次读取的行是否有找到匹配的StartKey
		for (int i = 0; i < children.size(); i++) {
			Container child = children.get(i);
			if (child instanceof Line) {
				Line line = (Line) child;

				/* return if line hasn't start key */
				if (!line.isHasStartKey())
					return true;

				foundStartKey = validate(line, reader);
			} else if (child instanceof Component) {
				Component c = (Component) child;
				foundStartKey = validate(c, reader);
			}
		}
		return foundStartKey;
	}

	/**
	 * NOTICE:The batch reader can't be closed.
	 */
	public static Object batchParse(Component component, BatchReader batchReader) throws ParseException {
		try {
			List<String> list = batchReader.readBatch();
			if (list == null || list.size() <= 0)
				return null;
			Object obj = parse(component, list);
			return obj;
		} catch (ParseException e) {

			throw e;
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	public static Object parse(Component component, List<String> contentList) throws ParseException {
		ReadStatus index = new ReadStatus();
		Map<String, Object> result = new HashMap<String, Object>();
		parseComponet(component, contentList, index, result, component.getConverter().isBatched());
		return result.get(component.getName());
	}

	private static void parseComponet(Component component, List<String> contentList, ReadStatus index, Object parent,
			boolean batched) throws ParseException {
		parseOnceComponet(component, contentList, index, parent, batched);
		if (component.isShowMany() || component.isShowNoneMany()) {
			if (index.lineNum() < contentList.size()) {
				String lineContent = contentList.get(index.lineNum());

				/* loop component */
				while (component.isBelongToMe(lineContent)) {
					parseOnceComponet(component, contentList, index, parent, batched);
					if (index.lineNum() >= contentList.size())
						break;
					lineContent = contentList.get(index.lineNum());
				}
			}
		}
	}

	private static void parseOnceComponet(Component component, List<String> contentList, ReadStatus index,
			Object parent, boolean batched) throws ParseException {
		Object obj = component.newDataInstance();
		List<Container> children = component.getChildren();
		for (int i = 0; i < children.size(); i++) {
			Container child = children.get(i);
			if (child instanceof Line) {
				Line line = (Line) child;
				parseLine(obj, line, contentList, index, batched);
			} else if (child instanceof Component) {
				Component c = (Component) child;
				parseComponet(c, contentList, index, obj, batched);
			}
		}
		ParseUtil.setValue(component.getName(), parent, obj);
	}

	private static boolean validate(Line line, IndexReader reader) throws IOException, ParseException {
		String content = reader.readLine();
		if (content == null) {
			if (line.isShowMany() || line.isShowOnce()) {
				throw new ParseException("[line:" + reader.lineNum() + "]Null line! expect line[startKey:"
						+ line.getStartKey() + "]!");
			}
			reader.back();
			return true;
		}
		if (!line.isBelongToMe(content)) {
			if (line.isShowMany() || line.isShowOnce())
				throw new ParseException("[line:" + reader.lineNum() + "] Unexpected line! invalid startKey!");
			else {
				reader.back();
				return false;
			}
		}

		validateContentLength(line, content, reader);

		if (line.isShowMany() || line.isShowNoneMany()) {
			String newLine = reader.readLine();
			if (newLine != null) {
				while (line.isBelongToMe(newLine)) {
					validateContentLength(line, newLine, reader);

					newLine = reader.readLine();
					if (newLine == null) {
						/* go back a line */
						reader.back();
						return true;
					}
				}
			}
			/* go back a line */
			reader.back();
		}
		String lastLine = reader.readLine();
		reader.back();
		return "".equalsIgnoreCase(lastLine) || lastLine == null || line.isBelongToMe(lastLine);
	}

	private static void validateContentLength(Line line, String content, IndexReader reader) throws ParseException {
		try {
			line.validateLength(content);
		} catch (ParseException e) {
			throw new ParseException("[index:" + reader.lineNum() + "]" + e.getMessage(), e);
		}
	}

	private static Object parseLine(Object target, Line line, List<String> contentList, ReadStatus index,
			boolean batched) throws ParseException {
		if (index.lineNum() >= contentList.size()) {
			/* not valid if batched */
			if (!batched && (line.isShowMany() || line.isShowOnce()))
				throw new ParseException("[index:" + index.lineNum() + "]Null line! expect line[startKey:"
						+ line.getStartKey() + "]!");
			else
				return target;
		}

		String content = contentList.get(index.lineNum());
		if (!line.isBelongToMe(content)) {
			/* not valid if batched */
			if (!batched && (line.isShowMany() || line.isShowOnce()))
				throw new ParseException("[index:" + index.lineNum() + "] Unexpected line! expect line[startKey:"
						+ line.getStartKey() + "]!\r\n[line:" + content + "]");
			else
				return target;
		}
		String mapkey = line.getName();
		Object obj = parseLineContent(line, content, index);
		int sameLineIndex = 0;
		if (line.isShowMany() || line.isShowNoneMany()) {
			ParseUtil.setValue(mapkey + sameLineIndex, target, obj);
			if (index.lineNum() < contentList.size()) {
				String newLine = contentList.get(index.lineNum());
				while (line.isBelongToMe(newLine)) {
					obj = parseLineContent(line, newLine, index);
					sameLineIndex++;
					ParseUtil.setValue(mapkey + sameLineIndex, target, obj);
					if (index.lineNum() >= contentList.size()) {
						break;
					}
					newLine = contentList.get(index.lineNum());
				}
			}
		} else {
			ParseUtil.setValue(mapkey, target, obj);
		}
		return target;
	}

	private static Object parseLineContent(Line line, String content, ReadStatus index) throws ParseException {
		/* increase index after parsing */
		index.increaseLineNum();
		Object parse = line.parse(content, index.lineNum());
		return parse;
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

	public static String obj2xml(Object obj, Component component) throws OgnlException, BuildException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append(renderComponent(obj, component));
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	private static StringBuffer renderComponent(Object obj, Component component) throws OgnlException, BuildException {
		StringBuffer buffer = new StringBuffer();
		if (component.isShowOnce() || component.isShowNoneOnce())
			buffer.append(renderOnceComponent(obj, component));
		else {
			if (!(obj instanceof Collection)) {
				throw new BuildException("Expect a Collection result for component[" + component.getName() + "]!");
			}
			Collection collection = (Collection) obj;
			for (Iterator item = collection.iterator(); item.hasNext();) {
				Object childObject = (Object) item.next();
				buffer.append(renderOnceComponent(childObject, component));
			}
		}
		return buffer;
	}

	@SuppressWarnings("unchecked")
	private static StringBuffer renderOnceComponent(Object obj, Component component) throws OgnlException,
			BuildException {
		StringBuffer buffer = new StringBuffer();
		String name = component.getName();
		buffer.append("<" + name + " xmlns=\"" + component.getNamespace() + "\">");
		List<Container> children = component.getChildren();
		for (int i = 0; i < children.size(); i++) {
			Container child = children.get(i);
			Object childObj;
			if (obj instanceof List)
				childObj = obj;
			else
				childObj = OgnlUtil.getValue(child.getName(), obj);
			if (childObj == null)
				continue;

			if (child instanceof Line) {
				Line line = (Line) child;
				buffer.append(renderLine(childObj, line));
			} else if (child instanceof Component) {
				Component c = (Component) child;
				buffer.append(renderComponent(childObj, c));
			}
		}
		buffer.append("</" + name + ">");
		return buffer;
	}

	@SuppressWarnings("unchecked")
	private static StringBuffer renderLine(Object obj, Line line) throws OgnlException, BuildException {
		StringBuffer buffer = new StringBuffer();
		if (line.isShowNoneOnce() || line.isShowOnce()) {
			buffer.append(renderSingleLine(obj, line));
		} else {
			List<Object> objects = (List<Object>) obj;
			for (int i = 0; i < objects.size(); i++) {
				Object object = objects.get(i);
				if (object == null)
					System.out.println();
				buffer.append(renderSingleLine(object, line));
			}
		}
		return buffer;
	}

	private static StringBuffer renderSingleLine(Object obj, Line line) throws OgnlException, BuildException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<" + line.getName() + ">");
		List<Property> properties = line.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			Property property = properties.get(i);
			Object value = OgnlUtil.getValue(property.getName(), obj);
			String v = property.readableBuild(value);
			buffer.append("<" + property.getName() + ">");
			buffer.append(v);
			buffer.append("</" + property.getName() + ">");
		}
		buffer.append("</" + line.getName() + ">");
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
