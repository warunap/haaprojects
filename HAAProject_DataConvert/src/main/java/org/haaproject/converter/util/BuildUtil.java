/**
 * $Revision: 1.1 $
 * $Author: geln_yang $
 * $Date: 2010/05/31 14:14:35 $
 *
 * Author: Eric Yang
 * Date  : Jul 25, 2009 11:30:14 AM
 *
 */
package org.haaproject.converter.util;


/**
 * @author Eric Yang
 * @version 1.0
 */
public class BuildUtil {

	public static final String LEFT = "left";

	public static final String RIGHT = "right";

	public static String rightTrim(String s) {
		if (s == null || s.trim().length() == 0)
			return "";
		if (s.trim().length() == s.length())
			return s;
		if (!s.startsWith(" ")) {
			return s.trim();
		} else {
			return s.substring(0, s.indexOf(s.trim().substring(0, 1)) + s.trim().length());
		}
	}

	public static String leftTrim(String s) {
		if (s == null || s.trim().length() == 0)
			return "";
		if (s.trim().length() == s.length())
			return s;
		if (!s.startsWith(" ")) {
			return s;
		} else {
			return s.substring(s.indexOf(s.trim().substring(0, 1)));
		}
	}

	/**
	 * Build a string base on the source one.<br>
	 * If the length of the source string is less than the given length ,<br>
	 * fill in the result string with blanks .<br>
	 * If the given length is less than or equal to 0,return the trim result <br>
	 * of source string or a null string when it's null.<br>
	 * 
	 * @param value
	 *            the source string
	 * @param length
	 *            the length of the result string
	 * @param align
	 *            the position of the source string in the result string
	 */
	public static String buildString(String value, int length, String align) {
		if (length <= 0)
			return value == null ? "" : value.trim();

		if (value == null) {
			return blank(length);
		} else if (value.length() == length) {
			return value;
		} else if (value.length() < length) {
			if (align.equalsIgnoreCase(RIGHT)) {
				return blank(length - value.length()) + value;
			} else if (align.equalsIgnoreCase(LEFT)) {
				return value + blank(length - value.length());
			} else {
				throw new RuntimeException("Invalid align value \"" + align + "\"");
			}
		} else {
			throw new RuntimeException("The length of \"" + value + "\" is over " + length);
		}
	}

	/**
	 * Build a number format string. <br>
	 * If result source string has radix point, the given decimal and scale seemed to be 1 if it's 0
	 * 
	 * @param src
	 *            the source number string
	 * @param precision
	 *            the precision of the number
	 * @param scale
	 *            the length of the scale part of result string
	 * @param needRadixPoint
	 *            whether fill radix point in result string
	 * @param needFillZero
	 *            whether fill zero if the length of result string is greater than that of the source.
	 */
	public static String buildNumber(String src, int precision, int scale, boolean needRadixPoint, boolean needFillZero) {
		if (precision < 0 || scale < 0 || (precision - scale) < 0) {
			throw new RuntimeException("Invalid number format!");
		} else {
			int decimal = precision - scale;
			if (needRadixPoint) {
				decimal = decimal == 0 ? 1 : decimal;
			}
			if (src == null || src.trim().length() == 0) {
				String result = "";
				if (needFillZero)
					result += zero(decimal);
				if (needRadixPoint) {
					result = result.equals("") ? zero(decimal) : result;
					result += "." + zero(scale);
				} else if (needFillZero)
					result += zero(scale);
				return result.equals("") ? "0" : result;
			} else {
				src = src.trim();

				int dotPos = src.indexOf(".");
				if (dotPos == -1) {
					if (src.length() > decimal)
						throw new RuntimeException("The length of " + src + " is over " + decimal + "!");
					String result = "";
					if (needFillZero)
						result += zero(decimal - src.length());
					result += src;
					if (needRadixPoint && scale != 0)
						result += "." + zero(scale);
					else if (needFillZero)
						result += zero(scale);
					return result;

				} else {
					String integer = src.substring(0, dotPos);
					String dot = scale == 0 ? "" : src.substring(dotPos + 1, src.length());
					if (integer.length() > decimal)
						throw new RuntimeException("The length of " + integer + " is over " + decimal + "!");
					if (dot.length() > scale)
						dot = dot.substring(0, scale);
					String result = "";
					if (needFillZero)
						result += zero(decimal - integer.length());
					result += integer;
					if (needRadixPoint && scale != 0)
						result += ".";
					result += dot;
					if (needFillZero)
						result += zero(scale - dot.length());
					return result;
				}
			}
		}
	}

	public static String blank(int len) {
		String s = "";
		for (int i = 0; i < len; i++) {
			s += " ";
		}
		return s;
	}

	public static String zero(int len) {
		String s = "";
		for (int i = 0; i < len; i++) {
			s += "0";
		}
		return s;
	}

	public static void main(String[] args) {
		System.out.println(buildNumber("11133", 6, 0, true, true));
		System.out.println(buildNumber("111.5533", 6, 0, true, true));
	}
}
