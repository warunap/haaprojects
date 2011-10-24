/**
 * $Revision: 1.3 $
 * $Author: geln_yang $
 * $Date: 2011/09/02 13:14:51 $
 *
 * Author: Eric Yang
 * Date  : May 21, 2009 3:16:02 PM
 *
 */
package org.haa.dataconvert.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class Utils {

	private static final String DEFAULT_SPLITTER = ",";
	private static final String FAIL_ENCODE_REPLACEMENT = "\uFFFD";

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String validateFixedField(String pattern, String line, int start, int end, String errorMsg) {
		try {
			checkFixedField(pattern, line, start, end, errorMsg);
			return "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String validateUnfixedField(String pattern, String value, int fieldIndex, String errorMsg) {
		return validateUnfixedField(pattern, value, fieldIndex, DEFAULT_SPLITTER, errorMsg);
	}

	public static String validateUnfixedField(String pattern, String value, int fieldIndex, String splitter,
			String errorMsg) {
		try {
			checkUnfixedField(pattern, value, fieldIndex, splitter, errorMsg);
			return "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static void checkFixedField(String pattern, String line, int start, int end, String errorMsg)
			throws Exception {
		pattern = filterChars(pattern);
		String value = filterChars(line.substring(start - 1, end));
		if (!value.matches(pattern))
			throw new Exception("[value:" + value + "]" + errorMsg);
	}

	public static void checkUnfixedField(String pattern, String value, int fieldIndex, String errorMsg)
			throws Exception {
		checkUnfixedField(pattern, value, fieldIndex, DEFAULT_SPLITTER, errorMsg);
	}

	public static void checkUnfixedField(String pattern, String value, int fieldIndex, String splitter, String errorMsg)
			throws Exception {
		pattern = filterChars(pattern);
		value = filterChars(value);
		String[] fields = value.split(splitter);
		if (fields.length < fieldIndex)
			throw new Exception("Out of range!");
		String field = fields[fieldIndex - 1];
		if (!field.matches(pattern))
			throw new Exception("[value:" + value + "]" + errorMsg);
	}

	public static String validate(String pattern, String value, String errorMsg) {
		try {
			check(pattern, value, errorMsg);
			return "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static void check(String pattern, String value, String errorMsg) throws Exception {
		pattern = filterChars(pattern);
		value = filterChars(value);
		if (!value.matches(pattern))
			throw new Exception("[value:" + value + "]" + errorMsg);
	}

	private static String filterChars(String str) {
		str = str.replaceAll("\r", "");
		str = str.replaceAll("\n", "");
		return str;
	}

	public static boolean match(String value, String regex) {
		return value.matches(regex);

	}

	public static void checkCardId(String arg0, String errorMsg) throws Exception {
		if (arg0 == null)
			throw new Exception(errorMsg);

		String personId = ((String) arg0).toUpperCase();

		Pattern pattern = Pattern.compile("^\\p{Upper}{1}\\d{9}", Pattern.CASE_INSENSITIVE);
		final char[] FIRSTWORD = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		final int[] FIRSTWORDNUMBER = { 1, 10, 19, 28, 37, 46, 55, 64, 39, 73, 82, 2, 11, 20, 48, 29, 38, 47, 56, 65,
				74, 83, 21, 3, 12, 30 };

		Matcher matcher = pattern.matcher(personId);
		if (matcher.matches()) {
			int verifyNumber = 0;
			// 英文字母的轉碼數字
			verifyNumber = FIRSTWORDNUMBER[Arrays.binarySearch(FIRSTWORD, personId.charAt(0))];
			// 身份證字號第1～8個數字依次取出乘以8～1
			for (int i = 0; i < 8; i++) {
				verifyNumber += Character.digit(personId.charAt(i + 1), 10) * (8 - i);
			}
			// 10 - 上述總和除10之餘數
			verifyNumber = 10 - (verifyNumber % 10);
			if (verifyNumber == Character.digit(personId.charAt(9), 10) || verifyNumber == 10) {
				return;
			}
		}
		throw new Exception(errorMsg);
	}

	public static String getHexValue(int number) {
		return Integer.toHexString(number).toUpperCase();
	}

	/**
	 * 將字串中的每個全角字符後面加一個addChar字符. <br>
	 * When JDK decode char failed ,it will be replaced by char "\uFFFD". All "\uFFFD" are considered as a
	 * DoubleByteChar
	 */
	public static String addChar2SBCCase(String originalString, String addChar) {
		StringBuffer output = new StringBuffer();
		for (char c : originalString.toCharArray()) {
			String s = Character.toString(c);
			if (s.getBytes().length > 1 || FAIL_ENCODE_REPLACEMENT.equals(s)) {
				output.append(c);
				output.append(addChar);
			} else
				output.append(c);
		}
		return output.toString();
	}

	/**
	 * whether contain fail encode replaced char
	 */
	public static boolean containFailEncodeChar(String content) {
		return content.indexOf(FAIL_ENCODE_REPLACEMENT) > 0;
	}

	/**
	 * 先字串中的EBCDIC編碼的0E前面、0F後面加|字符，然後將字串中的每個全角字符後面加一個|字符
	 */
	public static String addChar2SBCCase(byte[] originalString) throws IOException {
		String str = convertBytes(originalString);
		StringBuffer output = new StringBuffer();
		for (char c : str.toCharArray()) {
			if (Character.toString(c).getBytes().length > 1) {
				output.append(c);
				output.append("|");
			} else
				output.append(c);
		}
		return output.toString();
	}

	public static String addChar2SBCCase(String originalString) throws IOException {
		originalString = convertBytes(originalString.getBytes("cp937"));
		StringBuffer output = new StringBuffer();
		for (char c : originalString.toCharArray()) {
			if (Character.toString(c).getBytes().length > 1) {
				output.append(c);
				output.append("|");
			} else
				output.append(c);
		}
		return output.toString();
	}

	public static String convertBytes(byte[] contentBytes) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			byte x0E = 14, x0F = 15, x40 = 79;// 79 '|' 124 '@'

			List<Byte> dbs = null;
			for (int b : contentBytes) {
				if (b == x0E) {
					bos.write(x40);
					if (dbs != null)
						continue;
					dbs = new ArrayList<Byte>();
				}
				bos.write(b);

				if (dbs != null)
					dbs.add(new Byte((byte) b));

				if (b == x0F) {
					if (dbs != null) {
						bos.write(x40);
						byte[] cb = checkDoubleByteString(dbs);
						if (cb != null && cb.length > 0)
							bos.write(cb);
						dbs = null;
					}
				}
			}
			bos.close();

			return bos.toString("cp937");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * handle Chinese character bytes
	 */
	private static byte[] checkDoubleByteString(List<Byte> dbs) {
		int dblen = dbs.size();
		if (dblen > 2) {
			// if dblen == 2 that mean the byte[] is {0E, 0F};
			// then we should not append any bytes.
			try {
				Byte[] bytearray = dbs.toArray(new Byte[0]);
				byte[] ba = new byte[bytearray.length];
				// to get the double byte array
				for (int i = 0; i < ba.length; i++)
					ba[i] = bytearray[i].byteValue();
				// transfer the double byte array to String and check the new
				// String's byte length
				int ckdblen = new String(ba, "cp937").getBytes().length;
				int diff = dblen - 2 - ckdblen;
				if (diff > 0) {
					// plz log the size of diff and ckdblen and dblen;
					byte[] app = new byte[diff];
					for (int i = 0; i < diff; i++)
						app[i] = 124;
					return app;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Utils.uuid());
		System.out.println(Utils.addChar2SBCCase("勞保費"));
	}

}
