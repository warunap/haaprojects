/**
 * $Revision: 1.20 $ $Author: geln_yang $ $Date: 2011/09/06 12:51:17 $
 * 
 * Author: Eric Yang Date : Jul 25, 2009 11:16:53 AM
 * 
 */
package org.haaproject.converter.dom;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.haaproject.converter.exception.BuildException;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.util.OgnlUtil;
import org.haaproject.converter.util.CharUtils;

import ognl.OgnlException;

/**
 * 不管有沒有設定正則的startKey，所有的property都要做設定
 * 
 * @author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Line extends Container {

	private static String TRANSLATE_CHAR = "|";

	/**
	 * The start key of the line,default to blank <br>
	 * if the start key is like "(regex_pattern)",use the regex pattern to match
	 * the line content
	 */
	private String startKey = "";

	/** Whether has start key */
	private boolean hasStartKey = true;

	/** The length of the line */
	private long length;

	/** Whether the line is fixed */
	private boolean fixed;

	/** The characters the line split with. */
	private String split;

	/** need SBC translate or not */
	private boolean trans = false;

	private boolean ignoreMore = false;

	private List<Property> properties = new ArrayList<Property>();

	private Map<String, Property> fieldMap = new HashMap<String, Property>();

	// --------functions------------
	public String build(Object obj) throws BuildException {
		StringBuffer buffer = new StringBuffer();

		if (hasStartKey && !isRegexStartKey()) {
			if (!fixed)
				buffer.append(startKey.trim() + split);
			else
				buffer.append(startKey);
		}

		for (int i = 0; i < properties.size(); i++) {
			Property field = properties.get(i);
			try {
				Object value = OgnlUtil.getValue(field.getName(), obj);
				String build = field.build(value);
				if (!fixed)
					build += split;

				buffer.append(build);
			} catch (OgnlException e) {
				throw new BuildException(e.getMessage(), e);
			}
		}
		if (!fixed)
			return buffer.substring(0, buffer.length() - split.length());

		return buffer.toString();
	}

	public Object parse(String content, int lineNo) throws ParseException {
		Object obj = newDataInstance();
		parse(obj, content, lineNo);
		return obj;
	}

	public void parse(Object obj, String content, int lineNo) throws ParseException {
		try {
			if (hasStartKey) {
				if (!isBelongToMe(content)) {
					throw new ParseException("[line:" + lineNo
							+ "]The line doesn't belong to the configuration! line content:" + content + "!");
				}
			}
			if (trans) {
				content = translate(content);
			}
			String[] fieldValues;
			if (fixed) {
				int contLenth = content.length();
				if (length > 0 && length != contLenth) {
					throw new ParseException("[line:" + lineNo + "]The length of the line doesn't equal " + length
							+ "!");
				}
				List<String> result = new ArrayList<String>();
				for (int i = 0; i < properties.size(); i++) {
					Property field = properties.get(i);
					String fvalue = "";
					if (length > 0)
						fvalue = content.substring(field.getStart(), field.getEnd());
					else {
						int endPos = Math.min(field.getEnd(), contLenth);
						if (field.getStart() <= (contLenth - 1))
							fvalue = content.substring(field.getStart(), endPos);
					}
					if (trans)
						fvalue = fvalue.replaceAll("\\" + TRANSLATE_CHAR, "");
					result.add(fvalue);
				}
				fieldValues = result.toArray(new String[] {});
			} else {
				fieldValues = splitLineContent(content);
				int expectLenth = properties.size();

				if (expectLenth != fieldValues.length) {
					throw new ParseException("[line:" + lineNo + "]Expect " + expectLenth + " values!");
				}
			}

			for (int i = 0; i < properties.size(); i++) {
				Property field = properties.get(i);
				Object val = field.parse(fieldValues[i], lineNo);
				OgnlUtil.setValue(field.getName(), obj, val);
			}
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}

	}

	public String[] splitLineContent(String lineContent) {
		if (ignoreMore) {
			String[] array = lineContent.split(split, properties.size());
			int index = array[array.length - 1].indexOf(",");
			if (index == 0) {
				array[array.length - 1] = "";
			} else if (index > 0) {
				array[array.length - 1] = array[array.length - 1].substring(0, index);
			}
			return array;
		} else {
			return lineContent.split(split, properties.size() + 2);
		}
	}

	public void addField(Property field) {
		Property oldField = fieldMap.get(field.getName());
		if (oldField == null) {
			properties.add(field);
			fieldMap.put(field.getName(), field);
			field.setLine(this);
		} else {
			// throw new ConfigurationException("Duplication filed named[" + field.getName() + "]");
		}
	}

	public boolean isRegexStartKey() {
		return startKey != null && startKey.startsWith("(") && startKey.endsWith(")");
	}

	public boolean isBelongToMe(String content) {
		if (!hasStartKey)
			return true;
		if (!isRegexStartKey())
			return content.startsWith(startKey);
		return content.matches(startKey);
	}

	public String translate(String content) {
		return CharUtils.addChar2SBCCase(content, TRANSLATE_CHAR);
	}

	public void validateLength(String content) throws ParseException {
		validateContentLength(this, content);
	}

	public static void validateContentLength(Line line, String content) throws ParseException {
		if (line.getLength() != 0 && line.getLength() != content.length()) {
			if (line.isTrans()) {
				try {
					if (line.getLength() != line.translate(content).length()) {
						if (!CharUtils.containFailEncodeChar(content)
								|| content.getBytes(line.getConverter().getCharset()).length < line.getLength()) {
							throw new ParseException("Unexpected length! expect line[length:" + line.getLength()
									+ "]!\r\ncontent[length:" + line.translate(content).length() + "]\r\n"
									+ content);
						}
					}
				} catch (UnsupportedEncodingException e) {
					throw new ParseException(e.getMessage());
				}
			} else {
				throw new ParseException("Unexpected length! expect line[length:" + line.getLength()
						+ "]!\r\ncontent[length:" + content.length() + "]\r\n" + content);
			}
		}
	}

	// -----------setter/getter------------

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getStartKey() {
		return startKey;
	}

	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public boolean isHasStartKey() {
		return hasStartKey;
	}

	public void setHasStartKey(boolean hasStartKey) {
		this.hasStartKey = hasStartKey;
	}

	public boolean isTrans() {
		return trans;
	}

	public void setTrans(boolean trans) {
		this.trans = trans;
	}

	public boolean isIgnoreMore() {
		return ignoreMore;
	}

	public void setIgnoreMore(boolean ignoreMore) {
		this.ignoreMore = ignoreMore;
	}

}
