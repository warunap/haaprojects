package org.haaprojects.website.info;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * Hello world!
 */
public class App {

	private static final String INFOKEY_ADDRESS = "Address";

	private static final String INFOKEY_PHONE = "Phone";

	private static final String INFOKEY_EMAIL = "Email";

	private static final String INFOKEY_CONTACEPAGEURL = "contactPageUrl";

	private static final String INFOKEY_SITENAME = "SiteName";

	private static final int THREAD_SIZE = 5;

	static DefaultHttpClient httpClient = createHttpClient();

	static Map<String, List<String>> collectConfigMap = new HashMap<String, List<String>>();

	private static void addconfig(String key, String regex, int groupNum) {
		List<String> value = new ArrayList<String>();
		value.add(regex);
		value.add(groupNum + "");
		collectConfigMap.put(key, value);
	}

	static {
		addconfig(INFOKEY_ADDRESS, "地址[：: ]*([^<> ]+)", 1);
		addconfig(INFOKEY_PHONE, "((\\d{3,4}-)(\\d{6,11})(-\\d{2,8})?(\\(\\d{2,8}\\))?)|(13\\d{9})", 0);
		addconfig(INFOKEY_EMAIL, "[a-zA-Z0-9]+[a-zA-Z0-9_\\-\\.]*[a-zA-Z0-9]+@([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,5}", 0);
	}

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
					synchronized (hostContainer) {
						running_thread_count++;
					}
					int collectcount = 0;
					DefaultHttpClient httpClient = createHttpClient();
					String host = null;
					while ((host = hostContainer.getNextHost()) != null) {
						try {
							if (collectcount++ > 50) {
								httpClient = createHttpClient();
								collectcount = 0;
							}
							collectSiteInfo(httpClient, dataMap, host);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
					synchronized (hostContainer) {
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
						Thread.sleep(3 * 1000);
					} catch (InterruptedException e) {
					}
					if (running_thread_count == 0) {
						try {
							writeResult(file, dataMap);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}

				System.out.println(">>> over");
			}

		}.start();

	}

	private static void writeResult(File file, Map<String, Map<String, String>> dataMap) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		buffer.append("<html><head><title>");
		String title = "Site Information " + time;
		buffer.append(title);
		buffer.append("</title></head><body>");
		buffer.append("<h3>" + title + "</h3>");
		buffer.append("<table border='1'><tr>");
		buffer.append("<th width='200'>HOST</th>");
		buffer.append("<th width='200'>" + INFOKEY_PHONE + "</th>");
		buffer.append("<th width='200'>" + INFOKEY_EMAIL + "</th>");
		buffer.append("<th>" + INFOKEY_SITENAME + "</th>");
		buffer.append("<th>" + INFOKEY_ADDRESS + "</th>");
		buffer.append("</tr>");
		Set<String> hostSet = dataMap.keySet();
		for (String host : hostSet) {
			Map<String, String> map = dataMap.get(host);
			Set<String> items = map.keySet();
			if (items.size() == 0) {
				continue;
			}
			buffer.append("<tr><td>");
			String url = map.get(INFOKEY_CONTACEPAGEURL);
			String siteName = getHtmlText(map, INFOKEY_SITENAME);
			String phone = getHtmlText(map, INFOKEY_PHONE);
			String email = getHtmlText(map, INFOKEY_EMAIL);
			String address = getHtmlText(map, INFOKEY_ADDRESS);

			if (StringUtils.isNotBlank(url)) {
				buffer.append("<a href=\"" + url + "\" target=\"blank\">" + host + "</a>");
			} else {
				buffer.append(host);
			}
			buffer.append("</td>");
			buffer.append("<td>" + phone + "</td>");
			buffer.append("<td>" + email + "</td>");
			if (siteName.length() > 50) {
				siteName = siteName.substring(0, 30);
			}
			buffer.append("<td>" + siteName + "</td>");
			buffer.append("<td>" + address + "</td>");
			buffer.append("</tr>");
		}
		buffer.append("</table>");

		buffer.append("</body></html>");

		String baseName = FilenameUtils.getBaseName(file.getName());
		String dir = file.getParent();
		FileUtils.write(new File(dir + baseName + "_output_" + time + ".html"), buffer.toString(), "UTF-8");

		System.out.println("--------------------------------");
		System.out.println(buffer.toString());
		System.out.println("over");
	}

	private static String getHtmlText(Map<String, String> map, String key) {
		String v = map.get(key);
		return v == null ? "" : v;
	}

	private static String getHost(String siteinfo) {
		String host = getMatchItems(siteinfo, "(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*((com)|(cn)|(tw))", -1);
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
		System.out.println(">>> collect :" + host);
		Map<String, String> map = dataMap.get(host);
		if (map == null) {
			map = new HashMap<String, String>();
			dataMap.put(host, map);
		}

		String content = getUrlContent(httpClient, host);
		String siteName = getFirstMatchItems(content, "<title>([^<>]+)</title>", 1);

		String contactPageUrl = getContactPageUrl(host, content);
		if (StringUtils.isNotBlank(contactPageUrl)) {
			content = getUrlContent(httpClient, contactPageUrl);
			map.put(INFOKEY_CONTACEPAGEURL, contactPageUrl);
		}
		Set<String> keySet = collectConfigMap.keySet();
		for (String key : keySet) {
			List<String> list = collectConfigMap.get(key);
			String regex = list.get(0);
			int groupNum = Integer.parseInt(list.get(1));
			String matchItems = getMatchItems(content, regex, groupNum);
			System.out.println(key + ":" + matchItems);
			if (StringUtils.isNotBlank(matchItems)) {
				map.put(key, matchItems);
			}
		}
		if (map.size() > 0 && StringUtils.isNotBlank(siteName)) {
			if (map.get(INFOKEY_CONTACEPAGEURL) == null) {
				map.put(INFOKEY_CONTACEPAGEURL, host);
			}
			siteName = siteName.replaceAll("\\s", "");
			map.put(INFOKEY_SITENAME, siteName);
		}
	}

	private static String getContactPageUrl(String host, String content) {
		String regex = "<a[^<>]+href=\"([^<>\"]+)\"[^<>]*>\\s*(联系我们|联系方式)\\s*</a>";
		String contactPageUrl = getFirstMatchItems(content, regex, 1);
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
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new IOException("get unexpected status code:" + statusCode + "," + host);
		}
		HttpEntity entity = response.getEntity();
		try {

			String entityContents = EntityUtils.toString(entity);
			String content = entityContents.toLowerCase(); // lower case
			try {
				String encode = getFirstMatchItems(content, "<meta\\s+http-equiv=\"content-type\"\\s+content=\"text/html;\\s+charset=([^<>\" ]+)\">", 1);
				if (StringUtils.isNotBlank(encode)) {
					content = new String(content.getBytes(encode), encode);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			return content;
		} finally {
			EntityUtils.consume(entity);
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

	private static String getMatchItems(String content, String phoneRegex, int groupNum) {
		Pattern pattern = Pattern.compile(phoneRegex);
		Matcher matcher = pattern.matcher(content);
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
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
			if (!first) {
				buffer.append(";");
			}
			buffer.append(item);
			first = false;
			matchCount++;
			if (matchCount > MAX_MATCH_SIZE) {
				break;
			}
		}
		return buffer.toString();
	}

	private static DefaultHttpClient createHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, 15 * 1000);
		return client;
	}
}
