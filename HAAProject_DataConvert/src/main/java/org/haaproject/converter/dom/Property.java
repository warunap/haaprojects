/**
 * $Revision: 1.17 $ $Author: geln_yang $ $Date: 2011/09/06 12:51:17 $
 * 
 * Author: Eric Yang Date : Jul 25, 2009 11:16:38 AM
 * 
 */
package org.haaproject.converter.dom;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.haaproject.converter.exception.BuildException;
import org.haaproject.converter.util.BuildUtil;

/**
 * @author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Property implements Serializable {

	public static final String LEFT = BuildUtil.LEFT;

	public static final String RIGHT = BuildUtil.RIGHT;

	public static final String TYPE_BIGDECIMAL = "BigDecimal";

	public static final String TYPE_STRING = "String";

	public static final String TYPE_INTEGER = "Integer";

	public static final String TYPE_LONG = "Long";

	public static final String TYPE_DATE = "Date";

	/** field name */
	private String name;

	/** property description */
	private String desc;

	// ------------fields for parse------------------
	/** the position the fields start */
	private int start;

	/** the position the fields end */
	private int end;

	/** the index number of the fields in one line */
	private int index;

	/**
	 * when the line is unfixed,the length of the value is dynamic,the "maxLength" defines the max length of the value
	 */
	private int maxLength;

	/** the pattern to validate the value */
	private String pattern;

	// ------------fields for build-------------------
	/** field type,default string */
	private String type = TYPE_STRING;

	/** field length */
	private int length;

	/** field align,used just when the field type is string */
	private String align = LEFT;

	/** field format,used when the field type is big_decimal or date */
	private String format;

	/**
	 * field precision,used when the field type is big_decimal or integer or long
	 */
	private int precision;

	/** field precision,used when the field type is big_decimal */
	private int scale;

	/**
	 * whether need fill with radix point,used when the field type is big_decimal
	 */
	private boolean needRadixPoint = false;

	/**
	 * whether need fill zero before,used when the field type is big_decimal or integer or long
	 */
	private boolean needFillZero = false;

	/** the default value of the field */
	private String defValue;

	/**
	 * whether the field is optional.<br>
	 * when the field value and the default value are null,<br>
	 * if the field is optional,the build value will be filled with blanks.<br>
	 * otherwise,throw null value exception.
	 */
	private boolean optional = false;

	/** whether to trim the field value when build ,default false */
	private boolean trim = false;

	private Line line;

	// --------------setter/getter-----------------
	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	// --------functions------------
	public String build(Object value) throws BuildException {
		return build(value, false);
	}

	public String readableBuild(Object value) throws BuildException {
		return build(value, true);
	}

	private String build(Object value, boolean readable) throws BuildException {
		String stringValue = "";
		if (value == null) {
			if (defValue != null) {
				stringValue = defValue;
			} else if (!optional) {
				throw new BuildException("Null value for field[" + name + "]!");
			}
		} else {
			stringValue = value.toString();
			if (TYPE_BIGDECIMAL.equals(type)) {
				if (value instanceof BigDecimal) {
					/* Avoid unexpected scientific notation value of BigDecimal */
					BigDecimal v = ((BigDecimal) value);
					String formatStr = format;
					if (format == null) {
						formatStr = BuildUtil.buildNumber("0", v.scale(), v.scale(), true, true);
					}
					DecimalFormat decimalFormat = new DecimalFormat(formatStr);
					stringValue = decimalFormat.format(v.doubleValue());
				}
			} else if (TYPE_DATE.equals(type)) {
				stringValue = value.toString();
				if (format != null) {
					stringValue = new SimpleDateFormat(format).format((Date) value);
				}
			}
		}

		String result = "";
		if (TYPE_LONG.equals(type) || TYPE_INTEGER.equals(type) || TYPE_BIGDECIMAL.equals(type)) {
			if (stringValue.equals("")) {
				stringValue = "0";
			}
			int _precision = precision > 0 ? precision : length;
			int _scale = TYPE_BIGDECIMAL.equals(type) ? scale : 0;
			boolean _needRadixPoint = readable ? true : needRadixPoint;
			boolean _needFillZero = readable ? false : needFillZero;

			result = BuildUtil.buildNumber(stringValue, _precision, _scale, _needRadixPoint, _needFillZero);
		} else {
			result = BuildUtil.buildString(stringValue, length, align);
		}

		if ((readable || !line.isFixed()) && trim)
			result = result.trim();

		return result;
	}

	public Object parse(String value, int lineNo) throws org.haaproject.converter.exception.ParseException {
		if (StringUtils.isEmpty(value) && optional) {
			if (defValue != null) {
				value = defValue;
			} else {
				return value;
			}
		}

		if (StringUtils.isBlank(pattern)
				&& (TYPE_LONG.equals(type) || TYPE_INTEGER.equals(type) || TYPE_BIGDECIMAL.equals(type))) {
			if (needRadixPoint)
				pattern = "0*[+-]?\\d+[\\.]?\\d*";
			else
				pattern = "0*[+-]?\\d+";
		}

		this.validate(value, lineNo);
		if (TYPE_STRING.equals(type))
			return value;
		else if (TYPE_LONG.equals(type))
			return Long.parseLong(removeNumBeforePlus(value));
		else if (TYPE_INTEGER.equals(type))
			return Integer.parseInt(removeNumBeforePlus(value));
		else if (TYPE_BIGDECIMAL.equals(type)) {
			if (!needRadixPoint && needFillZero)
				value = value.substring(0, precision - scale) + "." + value.substring(precision - scale);
			return new BigDecimal(removeNumBeforePlus(value));
		} else if (TYPE_DATE.equals(type)) {
			try {
				return new SimpleDateFormat(format).parse(value);
			} catch (ParseException e) {
				throw new org.haaproject.converter.exception.ParseException(e);
			}
		}
		return value;
	}

	private String removeNumBeforePlus(String value) {
		int index = value.indexOf("-");
		if (index != -1) {
			return value.substring(index);
		}
		index = value.indexOf("+");
		if (index != -1) {
			return value.substring(index);
		}
		return value;
	}

	public void validate(String val, int lineNo) throws org.haaproject.converter.exception.ParseException {
		if (StringUtils.isNotBlank(pattern) && !val.matches(pattern)) {
			StringBuffer sb = null;
			if (StringUtils.isBlank(desc))
				sb = new StringBuffer("[line:").append(lineNo).append("][field:").append(name).append("][value:")
						.append(val).append("] does not match [pattern:").append(pattern).append("]");
			else
				sb = new StringBuffer("[line:").append(lineNo).append("][value:").append(val).append("][").append(desc)
						.append("] does not match [pattern:").append(pattern).append("]");
			throw new org.haaproject.converter.exception.ParseException(sb.toString());
		}
	}

	// --------setter/getter------------
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getAlign() {
		return align;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}

	public boolean isNeedRadixPoint() {
		return needRadixPoint;
	}

	public boolean isNeedFillZero() {
		return needFillZero;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setPrecision(int percision) {
		this.precision = percision;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public void setNeedRadixPoint(boolean needRadixPoint) {
		this.needRadixPoint = needRadixPoint;
	}

	public void setNeedFillZero(boolean needFillZero) {
		this.needFillZero = needFillZero;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getIndex() {
		return index;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public boolean isTrim() {
		return trim;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
