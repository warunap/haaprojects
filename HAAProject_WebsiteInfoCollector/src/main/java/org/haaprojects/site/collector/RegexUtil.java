/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 6:25:34 PM
 */
package org.haaprojects.site.collector;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class RegexUtil {

	public static String getFirstMatchItems(String content, String regex, int groupNum) {
		String matchItems = getMatchItems(content, regex, groupNum);
		if (StringUtils.isNotBlank(matchItems)) {
			String[] arr = matchItems.split(";");
			return arr[0];
		}
		return null;
	}

	public static String getMatchItems(String content, String phoneRegex, int groupNum) {
		Pattern pattern = Pattern.compile(phoneRegex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);
		Set<String> items = new HashSet<String>();
		while (matcher.find()) {
			String item = "";
			if (groupNum < 1) {
				item = matcher.group();
			} else {
				try {
					item = matcher.group(groupNum);
				} catch (Exception e) {
					item = matcher.group();
				}
			}
			items.add(item);
		}
		if (items.size() > 0) {
			StringBuffer buffer = new StringBuffer();
			for (String item : items) {
				buffer.append(";");
				buffer.append(item);
			}
			return buffer.substring(1);
		}
		return "";
	}

}
