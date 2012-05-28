/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 5:57:45 PM
 */
package org.haaprojects.site.collector.generator;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaprojects.site.collector.Const;
import org.haaprojects.site.collector.data.CollectData;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class HtmlGenerator implements ISiteInfoGenerator {

	private static final Log logger = LogFactory.getLog(HtmlGenerator.class);

	long midTime = 5;
	long lessTime = 3;
	long moreTime = 7;

	public String generate(CollectData dataMap) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /><title>");
		String title = "Site Information";
		buffer.append(title);
		buffer.append("</title></head><body>");
		buffer.append("<h3>" + title + "</h3>");
		buffer.append("<table border='1'><tr>");
		buffer.append("<th width='200'>HOST</th>");
		buffer.append("<th width='130'>" + Const.INFOKEY_PHONE + "</th>");
		buffer.append("<th width='150'>" + Const.INFOKEY_EMAIL + "</th>");
		buffer.append("<th width='80'>" + Const.INFOKEY_QQ + "</th>");
		buffer.append("<th width='80'>" + Const.INFOKEY_LOADTIME + "</th>");
		buffer.append("<th>" + Const.INFOKEY_SITENAME + "</th>");
		buffer.append("<th>" + Const.INFOKEY_REGISTRATIONNUM + "</th>");
		buffer.append("</tr>");
		Set<String> hostUrlSet = dataMap.keySet();
		for (String hostUrl : hostUrlSet) {
			Map<String, String> map = dataMap.get(hostUrl);
			Set<String> items = map.keySet();
			if (items.size() == 0) {
				continue;
			}

			String host = new URL(hostUrl).getHost();
			String shorthost = host.replace("www.", "");

			String contactUrl = map.get(Const.INFOKEY_CONTACEPAGEURL);
			String aboutUsUrl = map.get(Const.INFOKEY_ABOUNTUSURL);
			String siteName = getHtmlText(map, Const.INFOKEY_SITENAME);
			String phone = getHtmlText(map, Const.INFOKEY_PHONE);
			String email = getHtmlText(map, Const.INFOKEY_EMAIL);
			String qq = getHtmlText(map, Const.INFOKEY_QQ);
			String loadTime = getHtmlText(map, Const.INFOKEY_LOADTIME);
			String registrationNum = getHtmlText(map, Const.INFOKEY_REGISTRATIONNUM);
			String trStyle = "";
			if (siteName.indexOf("厦门") != -1 || (registrationNum != null && registrationNum.indexOf("闽") != -1)) {
				trStyle += "background:lightblue;";
			} else if (StringUtils.isNotBlank(email) && email.indexOf(shorthost) == -1) {
				trStyle += "background:lightgreen;";
			}
			buffer.append("<tr style='" + trStyle + "'>");
			buffer.append("<td>");
			buffer.append("<a href=\"" + hostUrl + "\" target=\"blank\">" + hostUrl + "</a>");
			if (StringUtils.isNotBlank(contactUrl)) {
				buffer.append("<br/><a href=\"" + contactUrl + "\" target=\"blank\">联系我们</a>");
			}
			if (StringUtils.isNotBlank(aboutUsUrl)) {
				buffer.append("<br/><a href=\"" + aboutUsUrl + "\" target=\"blank\">关于我们</a>");
			}
			buffer.append("</td>");
			buffer.append("<td>" + phone.replaceAll(";", "<br/>") + "</td>");
			buffer.append("<td>" + email.replaceAll(";", "<br/>") + "</td>");
			buffer.append("<td>" + qq.replaceAll(";", "<br/>") + "</td>");

			String style = "";
			String speedDesc = getSpeedDesc(Long.parseLong(loadTime), moreTime, midTime, lessTime);
			if ("慢".equals(speedDesc)) {
				style = " style=\"background:yellow;\"";
			} else if ("差".equals(speedDesc)) {
				style = " style=\"background:red;\"";
			}
			buffer.append("<td" + style + ">" + speedDesc + "(" + loadTime + "s)</td>");

			if (siteName.length() > 50) {
				siteName = siteName.substring(0, 30);
			}
			buffer.append("<td>" + siteName + "</td>");
			buffer.append("<td>" + registrationNum + "</td>");
			buffer.append("</tr>");
		}
		buffer.append("</table>");

		buffer.append("</body></html>");

		logger.debug(buffer);
		return buffer.toString();

	}

	private static String getSpeedDesc(long time, long moreTime, long midTime, long lessTime) {
		if (time > midTime) {
			if (time > moreTime) {
				return "差";
			} else {
				return "慢";
			}
		} else {
			if (time > lessTime) {
				return "一般";
			} else {
				return "快";
			}
		}
	}

	private static String getHtmlText(Map<String, String> map, String key) {
		String v = map.get(key);
		return v == null ? "" : v;
	}
}
