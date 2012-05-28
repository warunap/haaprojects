/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 6:23:14 PM
 */
package org.haaprojects.site.collector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class RegexConfig {

	private static Map<String, Map<String, Object>> collectConfigMap = new HashMap<String, Map<String, Object>>();

	private static final String KEY_REGEX = "regex";
	private static final String KEY_REGEXGROUPNUM = "groupnum";
	static {
		addconfig(Const.INFOKEY_TITLE, "<title>([^<>]+)</title>", 1);
		addconfig(Const.INFOKEY_REGISTRATIONNUM, ".ICP(备|证)[:：]?\\d+号", 0);
		addconfig(Const.INFOKEY_ADDRESS, "(地址|address|addr|add)[：: ]?([^<>\\s]+)", 2);
		addconfig(Const.INFOKEY_QQ, "qq[：:][ ]?(\\d{6,})", 1);
		addconfig(Const.INFOKEY_PHONE, "((\\d{3,4}-)(\\d{6,11})(-\\d{2,8})?(\\(\\d{2,8}\\))?)|(13\\d{9})", 0);
		addconfig(Const.INFOKEY_EMAIL, "[a-zA-Z0-9]+[a-zA-Z0-9_\\-\\.]*[a-zA-Z0-9]+@([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,5}", 0);
		addconfig(Const.INFOKEY_CONTACEPAGEURL, "<a[^<>]+href=\"([^<>\"]+)\"[^<>]*>\\s*((联系我们)|(联系方式)|(contact us))\\s*</a>", 1);
		addconfig(Const.INFOKEY_ABOUNTUSURL, "<a[^<>]+href=\"([^<>\"']+)\"[^<>]*>\\s*((关于我们)|(about us))\\s*</a>", 1);
	}

	public static void addconfig(String key, String regex, int groupNum) {
		Map<String, Object> value = new HashMap<String, Object>();
		value.put(KEY_REGEX, regex);
		value.put(KEY_REGEXGROUPNUM, groupNum);
		collectConfigMap.put(key, value);
	}

	public static String getRegex(String infokey) {
		Map<String, Object> map = collectConfigMap.get(infokey);
		return (String) (map == null ? null : map.get(KEY_REGEX));
	}

	public static int getRegexGroupNum(String infokey) {
		Map<String, Object> map = collectConfigMap.get(infokey);
		String val = "0";
		if (map != null) {
			val = map.get(KEY_REGEXGROUPNUM).toString();
		}
		return Integer.parseInt(val);
	}
}
