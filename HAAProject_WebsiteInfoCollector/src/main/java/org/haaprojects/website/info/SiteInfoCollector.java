package org.haaprojects.website.info;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.haaprojects.http.HaaHttpClient;

/**
 * Hello world!
 */
public class SiteInfoCollector {

	private static final Log logger = LogFactory.getLog(SiteInfoCollector.class);

	public static final String INFOKEY_TITLE = "Title";
	public static final String INFOKEY_ADDRESS = "Address";
	public static final String INFOKEY_PHONE = "Phone";
	public static final String INFOKEY_EMAIL = "Email";
	public static final String INFOKEY_CONTACEPAGEURL = "ContactPageUrl";
	public static final String INFOKEY_ABOUNTUSURL = "AbountUsUrl";
	public static final String INFOKEY_SITENAME = "SiteName";
	public static final String INFOKEY_LOADTIME = "Speed";
	public static final String INFOKEY_REGISTRATIONNUM = "RegistrationNumber";

	public static final int THREAD_SIZE = 5;
	private static final String KEY_REGEX = "regex";
	private static final String KEY_REGEXGROUPNUM = "groupnum";

	public static long maxLoadTime = 0; // inital to the min
	public static long minLoadTime = 100; // inital to the max

	private static Map<String, Map<String, Object>> collectConfigMap = new HashMap<String, Map<String, Object>>();

	private static void addconfig(String key, String regex, int groupNum) {
		Map<String, Object> value = new HashMap<String, Object>();
		value.put(KEY_REGEX, regex);
		value.put(KEY_REGEXGROUPNUM, groupNum);
		collectConfigMap.put(key, value);
	}

	private static String getRegex(String infokey) {
		Map<String, Object> map = collectConfigMap.get(infokey);
		return (String) (map == null ? null : map.get(KEY_REGEX));
	}

	private static int getRegexGroupNum(String infokey) {
		Map<String, Object> map = collectConfigMap.get(infokey);
		return Integer.parseInt((String) (map == null ? "0" : map.get(KEY_REGEXGROUPNUM)));
	}

	static {
		addconfig(INFOKEY_TITLE, "<title>([^<>]+)</title>", 1);
		addconfig(INFOKEY_REGISTRATIONNUM, ".ICP(备|证)[:：]?\\d+号", 0);
		addconfig(INFOKEY_ADDRESS, "(地址|address|addr|add)[：: ]?([^<>\\s]+)", 2);
		addconfig(INFOKEY_PHONE, "((\\d{3,4}-)(\\d{6,11})(-\\d{2,8})?(\\(\\d{2,8}\\))?)|(13\\d{9})", 0);
		addconfig(INFOKEY_EMAIL, "[a-zA-Z0-9]+[a-zA-Z0-9_\\-\\.]*[a-zA-Z0-9]+@([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,5}", 0);
		addconfig(INFOKEY_CONTACEPAGEURL, "<a[^<>]+href=\"([^<>\"]+)\"[^<>]*>\\s*((联系我们)|(联系方式)|(contact us))\\s*</a>", 1);
		addconfig(INFOKEY_ABOUNTUSURL, "<a[^<>]+href=\"([^<>\"']+)\"[^<>]*>\\s*((关于我们)|(about us))\\s*</a>", 1);
	}

	static String running_thread_lock = "LOCK";
	static int running_thread_count = 0;

	interface HostContainer {
		public String getNextHost();
	}

	public static void main(String[] args) throws Exception {
		final Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
		String filePath = args[0];
		final File file = new File(filePath);
		final List<String> lines = FileUtils.readLines(file, "UTF-8");
		final HostContainer hostContainer = new HostContainer() {
			int readIndex = 0;

			public synchronized String getNextHost() {
				String siteinfo = null;
				while (readIndex < lines.size()) {
					siteinfo = lines.get(readIndex++);
					if (siteinfo != null) {
						siteinfo = siteinfo.toLowerCase();
						String host = getHost(siteinfo);
						if (host != null) {
							return host;
						}
					}
				}
				return null;
			}
		};

		/* start thread to collect */
		for (int i = 0; i < THREAD_SIZE; i++) {
			new Thread() {

				public void run() {
					synchronized (running_thread_lock) {
						running_thread_count++;
					}
					try {
						String host = null;
						while ((host = hostContainer.getNextHost()) != null) {
							DefaultHttpClient httpClient = createHttpClient();
							try {
								collectSiteInfo(httpClient, dataMap, host);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							} finally {
								try {
									httpClient.getConnectionManager().shutdown();
								} catch (Exception e) {
								}
							}
						}

					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}

					synchronized (running_thread_lock) {
						running_thread_count--;
					}
				}

			}.start();
		}

		/* write to file */
		new Thread() {

			public void run() {
				while (true) {

					try {
						logger.info("wait collect finish ...");
						Thread.sleep(3 * 1000);
					} catch (InterruptedException e) {
					}
					synchronized (running_thread_lock) {
						System.out.println(running_thread_count);
						if (running_thread_count == 0) {
							try {
								writeResult(file, dataMap);
							} catch (IOException e) {
								logger.error(e.getMessage(), e);
							}
							break;
						}
					}
				}

				logger.info(">>> over");
			}

		}.start();

	}

	private static void writeResult(File file, Map<String, Map<String, String>> dataMap) throws IOException {
		long midTime = (maxLoadTime + minLoadTime) / 2;
		long lessTime = (midTime + minLoadTime) / 2;
		long moreTime = (midTime + maxLoadTime) / 2;

		StringBuffer buffer = new StringBuffer();
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		buffer.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /><title>");
		String title = "Site Information " + time;
		buffer.append(title);
		buffer.append("</title></head><body>");
		buffer.append("<h3>" + title + "</h3>");
		buffer.append("<table border='1'><tr>");
		buffer.append("<th width='200'>HOST</th>");
		buffer.append("<th width='200'>" + INFOKEY_PHONE + "</th>");
		buffer.append("<th width='200'>" + INFOKEY_EMAIL + "</th>");
		buffer.append("<th width='200'>" + INFOKEY_LOADTIME + "</th>");
		buffer.append("<th>" + INFOKEY_SITENAME + "</th>");
		buffer.append("<th>" + INFOKEY_REGISTRATIONNUM + "</th>");
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

			String contactUrl = map.get(INFOKEY_CONTACEPAGEURL);
			String aboutUsUrl = map.get(INFOKEY_ABOUNTUSURL);
			String siteName = getHtmlText(map, INFOKEY_SITENAME);
			String phone = getHtmlText(map, INFOKEY_PHONE);
			String email = getHtmlText(map, INFOKEY_EMAIL);
			String loadTime = getHtmlText(map, INFOKEY_LOADTIME);
			String registrationNum = getHtmlText(map, INFOKEY_REGISTRATIONNUM);
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

		String baseName = FilenameUtils.getBaseName(file.getName());
		String dir = file.getParent();
		FileUtils.write(new File(dir + baseName + "_output_" + time + ".html"), buffer.toString(), "UTF-8");

		logger.info("--------------------------------");
		logger.info(buffer.toString());
		logger.info("over");
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

	private static String getHost(String siteinfo) {
		String host = getMatchItems(siteinfo, "\\s*(([a-zA-Z0-9\\-]+\\.)+(com|cn))\\s*", 1);
		if (StringUtils.isNotBlank(host)) {
			String[] arr = host.split(";");
			host = arr[0];

			if (!StringUtils.startsWith(host, "http")) {
				host = "http://" + host;
			}
			return host;
		}
		return null;
	}

	private static void collectSiteInfo(DefaultHttpClient httpClient, Map<String, Map<String, String>> dataMap, String host) throws IOException {
		logger.info(">>> collect :" + host);
		Map<String, String> map = dataMap.get(host);
		if (map == null) {
			map = new HashMap<String, String>();
			dataMap.put(host, map);
		}

		long start = System.currentTimeMillis();
		String content = getUrlContent(httpClient, host);
		long end = System.currentTimeMillis();

		long speed = (end - start) / 1000;
		map.put(INFOKEY_LOADTIME, speed + "");
		if (speed > maxLoadTime) {
			maxLoadTime = speed;
		}
		if (speed < minLoadTime) {
			minLoadTime = speed;
		}
		String siteName = getFirstMatchItemsByInfokey(content, INFOKEY_TITLE);
		String registrationNum = getFirstMatchItemsByInfokey(content, INFOKEY_REGISTRATIONNUM);
		String aboutUsUrl = getFirstMatchItemsByInfokey(content, INFOKEY_ABOUNTUSURL);

		String contactPageUrl = getContactPageUrl(host, content);

		if (StringUtils.isNotBlank(contactPageUrl)) {
			content = getUrlContent(httpClient, contactPageUrl);
			map.put(INFOKEY_CONTACEPAGEURL, contactPageUrl);
			if (StringUtils.isBlank(siteName)) {
				siteName = getFirstMatchItemsByInfokey(content, INFOKEY_TITLE);
			}
			if (StringUtils.isBlank(registrationNum)) {
				registrationNum = getFirstMatchItemsByInfokey(content, INFOKEY_REGISTRATIONNUM);
			}
			if (StringUtils.isBlank(aboutUsUrl)) {
				aboutUsUrl = getFirstMatchItemsByInfokey(content, INFOKEY_ABOUNTUSURL);
			}
		}

		if (StringUtils.isNotBlank(siteName)) {
			siteName = siteName.replaceAll("\\s", "");
			map.put(INFOKEY_SITENAME, siteName);
		}
		if (StringUtils.isNotBlank(registrationNum)) {
			map.put(INFOKEY_REGISTRATIONNUM, registrationNum);
		}
		if (StringUtils.isNotBlank(aboutUsUrl)) {
			map.put(INFOKEY_ABOUNTUSURL, aboutUsUrl);
		}

		String phones = getMatchItemsByInfokey(content, INFOKEY_PHONE);
		if (StringUtils.isNotBlank(phones)) {
			map.put(INFOKEY_PHONE, phones);
		}
		String emails = getMatchItemsByInfokey(content, INFOKEY_EMAIL);
		if (StringUtils.isNotBlank(emails)) {
			map.put(INFOKEY_EMAIL, emails);
		}

		logger.info(host + ":" + map);
	}

	private static String getContactPageUrl(String host, String content) {
		String contactPageUrl = getFirstMatchItemsByInfokey(content, INFOKEY_CONTACEPAGEURL);

		if (StringUtils.isBlank(contactPageUrl)) {
			String regex = "window\\.location\\.href=([^;=\\s]+)";
			contactPageUrl = getFirstMatchItems(content, regex, 1);
			if (StringUtils.isNotBlank(contactPageUrl)) {
				contactPageUrl = contactPageUrl.replace("\"", "").replace("\'", "");
			}
		}

		if (StringUtils.isNotBlank(contactPageUrl)) {
			if (contactPageUrl.startsWith("http")) {
				return contactPageUrl;
			} else {
				if (contactPageUrl.startsWith("/")) {
					return host + contactPageUrl;
				} else if (contactPageUrl.startsWith("mailto:")) {
					return null;
				} else {
					return host + "/" + contactPageUrl;
				}
			}
		}
		return null;
	}

	private static String getUrlContent(DefaultHttpClient httpClient, String host) throws IOException {
		HttpGet request = new HttpGet(host);
		request.addHeader("Accept-Charset", "utf-8");
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				throw new IOException("get unexpected status code:" + statusCode + "," + host);
			}
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			InputStream is = entity.getContent();
			int size = 0;
			while ((size = is.read(bytes)) != -1) {
				bos.write(bytes, 0, size);
			}
			is.close();
			byte[] htmlByts = bos.toByteArray();
			FileUtils.writeByteArrayToFile(new File("d:/temp.html"), htmlByts);
			String content = new String(htmlByts, "utf-8").toLowerCase(); // lower case
			try {
				String encode = getFirstMatchItems(content, "<meta\\s+http\\-equiv=\"content\\-type\"\\s+content=\"text/html;\\s+charset=([^<>\" ]+)\"", 1);
				if (StringUtils.isNotBlank(encode) && !"utf-8".equalsIgnoreCase(encode) && !"utf8".equalsIgnoreCase(encode)) {
					content = new String(htmlByts, encode).toLowerCase();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return content;
		} finally {
			EntityUtils.consume(entity);
		}
	}

	private static String getFirstMatchItemsByInfokey(String content, String infoKey) {
		try {
			return getFirstMatchItems(content, getRegex(infoKey), getRegexGroupNum(infoKey));
		} catch (Exception e) {
			return "";
		}
	}

	private static String getMatchItemsByInfokey(String content, String infoKey) {
		try {
			return getMatchItems(content, getRegex(infoKey), getRegexGroupNum(infoKey));
		} catch (Exception e) {
			return "";
		}
	}

	private static String getFirstMatchItems(String content, String regex, int groupNum) {
		String matchItems = getMatchItems(content, regex, groupNum);
		if (StringUtils.isNotBlank(matchItems)) {
			String[] arr = matchItems.split(";");
			return arr[0];
		}
		return null;
	}

	static int MAX_MATCH_SIZE = 3;

	public static String getMatchItems(String content, String phoneRegex, int groupNum) {
		Pattern pattern = Pattern.compile(phoneRegex);
		Matcher matcher = pattern.matcher(content);
		Set<String> items = new HashSet<String>();
		int matchCount = 0;
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
			matchCount++;
			if (matchCount > MAX_MATCH_SIZE) {
				break;
			}
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

	private static DefaultHttpClient createHttpClient() {
		return new HaaHttpClient();
	}

}
