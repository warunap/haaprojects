/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 5:57:45 PM
 */
package org.haaprojects.site.collector.generator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaprojects.site.collector.Const;
import org.haaprojects.site.collector.data.CollectData;
import org.haaprojects.site.collector.data.SiteInfo;

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

		Set<String> hostUrlSet = dataMap.keySet();
		List<SiteInfo> siteListHasSelfMail = new ArrayList<SiteInfo>();
		List<SiteInfo> siteListNoSelfMail = new ArrayList<SiteInfo>();
		for (String hostUrl : hostUrlSet) {
			SiteInfo siteInfo = dataMap.get(hostUrl);
			if (siteInfo.size() == 0) {
				continue;
			}
			String host = new URL(hostUrl).getHost();
			String shorthost = host.replace("www.", "");

			String email = siteInfo.get(Const.INFOKEY_EMAIL);
			if (StringUtils.isNotBlank(email) && email.indexOf(shorthost) == -1) {
				siteListHasSelfMail.add(siteInfo);
			} else {
				siteListNoSelfMail.add(siteInfo);
			}
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /><title>");
		String title = "Site Information";
		buffer.append(title);
		buffer.append("</title></head><body>");
		buffer.append("<h3>Site List With self-mail</h3>");
		buffer.append(buildSiteInfoTable(siteListHasSelfMail));
		buffer.append("<h3>Site List No self-mail</h3>");
		buffer.append(buildSiteInfoTable(siteListNoSelfMail));
		buffer.append("</body></html>");

		logger.debug(buffer);
		return buffer.toString();

	}

	private StringBuffer buildSiteInfoTable(List<SiteInfo> siteList) {

		StringBuffer buffer = new StringBuffer();
		if (siteList != null && siteList.size() > 0) {
			buffer.append("<table border='1'><tr>");
			buffer.append("<th width='200'>HOST</th>");
			buffer.append("<th width='130'>" + Const.INFOKEY_PHONE + "</th>");
			buffer.append("<th width='150'>" + Const.INFOKEY_EMAIL + "</th>");
			buffer.append("<th width='80'>" + Const.INFOKEY_QQ + "</th>");
			buffer.append("<th width='80'>" + Const.INFOKEY_LOADTIME + "</th>");
			buffer.append("<th>" + Const.INFOKEY_SITENAME + "</th>");
			buffer.append("<th>" + Const.INFOKEY_REGISTRATIONNUM + "</th>");
			buffer.append("</tr>");
			for (SiteInfo siteInfo : siteList) {
				String hostUrl = siteInfo.getHostUrl();
				String contactUrl = siteInfo.get(Const.INFOKEY_CONTACEPAGEURL);
				String aboutUsUrl = siteInfo.get(Const.INFOKEY_ABOUNTUSURL);
				String siteName = getHtmlText(siteInfo, Const.INFOKEY_SITENAME);
				String phone = getHtmlText(siteInfo, Const.INFOKEY_PHONE);
				String email = getHtmlText(siteInfo, Const.INFOKEY_EMAIL);
				String qq = getHtmlText(siteInfo, Const.INFOKEY_QQ);
				String loadTime = getHtmlText(siteInfo, Const.INFOKEY_LOADTIME);
				String registrationNum = getHtmlText(siteInfo, Const.INFOKEY_REGISTRATIONNUM);
				String trStyle = "";
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
		}
		return buffer;
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
