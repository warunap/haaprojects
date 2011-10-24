/**
 * $Revision: 1.1 $
 * $Author: geln_yang $
 * $Date: 2010/05/31 14:14:35 $
 *
 * Author: Eric Yang
 * Date  : May 31, 2010 8:12:43 PM
 *
 */
package org.haa.dataconvert.util;

import java.util.List;

import org.haa.dataconvert.exception.ParseException;


/**
 * @Author Eric Yang
 * @version 1.0
 */
public class ParseUtil {

	@SuppressWarnings("unchecked")
	public static void setValue(String key, Object target, Object value) throws ParseException {
		try {
			if (target instanceof List) {
				List t = (List) target;
				t.add(value);
			} else {
				OgnlUtil.setValue(key, target, value);
			}
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}
}
