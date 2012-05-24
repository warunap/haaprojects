package org.haaprojects.website.info;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.haaprojects.http.HaaHttpClient;

/**
 * Hello world!
 */
public class SiteInfoCollector {

	private static final Log logger = LogFactory.getLog(SiteInfoCollector.class);

	public static final String INFOKEY_ADDRESS = "Address";

	public static final String INFOKEY_PHONE = "Phone";

	public static final String INFOKEY_EMAIL = "Email";

	public static final String INFOKEY_CONTACEPAGEURL = "ContactPageUrl";

	public static final String INFOKEY_SITENAME = "SiteName";

	public static final String INFOKEY_SPEED = "SPEED";

	public static final int THREAD_SIZE = 5;

	public static final String INFOKEY_REGISTRATIONNUM = "RegistrationNumber";

	public static long LOWEST_SPEED = 0;

	static Map<String, List<String>> collectConfigMap = new HashMap<String, List<String>>();

	private static void addconfig(String key, String regex, int groupNum) {
		List<String> value = new ArrayList<String>();
		value.add(regex);
		value.add(groupNum + "");
		collectConfigMap.put(key, value);
	}

	static {
		// addconfig(INFOKEY_ADDRESS, "(地址|address|addr)[：: ]*([^<>\\s]+)", 2);
		addconfig(INFOKEY_PHONE, "((\\d{3,4}-)(\\d{6,11})(-\\d{2,8})?(\\(\\d{2,8}\\))?)|(13\\d{9})", 0);
		addconfig(INFOKEY_EMAIL, "[a-zA-Z0-9]+[a-zA-Z0-9_\\-\\.]*[a-zA-Z0-9]+@([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,5}", 0);
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
					String threadId = this.getThreadGroup().getName() + "_" + this.getName();
					System.out.println(threadId);
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

		long midSpeed = LOWEST_SPEED / 2;
		long goodSpeed = midSpeed / 2;
		long lowerSpeed = (midSpeed + LOWEST_SPEED) / 2;

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
		buffer.append("<th width='200'>" + INFOKEY_SPEED + "</th>");
		buffer.append("<th>" + INFOKEY_SITENAME + "</th>");
		buffer.append("<th>" + INFOKEY_REGISTRATIONNUM + "</th>");
		// buffer.append("<th>" + INFOKEY_ADDRESS + "</th>");
		buffer.append("</tr>");
		Set<String> hostSet = dataMap.keySet();
		for (String host : hostSet) {
			Map<String, String> map = dataMap.get(host);
			Set<String> items = map.keySet();
			if (items.size() == 0) {
				continue;
			}

			String contactUrl = map.get(INFOKEY_CONTACEPAGEURL);
			String siteName = getHtmlText(map, INFOKEY_SITENAME);
			String phone = getHtmlText(map, INFOKEY_PHONE);
			String email = getHtmlText(map, INFOKEY_EMAIL);
			// String address = getHtmlText(map, INFOKEY_ADDRESS);
			String speed = getHtmlText(map, INFOKEY_SPEED);
			String registrationNum = getHtmlText(map, INFOKEY_REGISTRATIONNUM);

			if (siteName.indexOf("厦门") != -1 || (registrationNum != null && registrationNum.indexOf("闽") != -1)) {
				buffer.append("<tr style='background:lightblue;'>");
			} else {
				buffer.append("<tr>");
			}
			buffer.append("<td>");
			buffer.append("<a href=\"" + host + "\" target=\"blank\">" + host + "</a>");
			if (StringUtils.isNotBlank(contactUrl)) {
				buffer.append("<br/><a href=\"" + contactUrl + "\" target=\"blank\">联系我们</a>");
			}
			buffer.append("</td>");
			buffer.append("<td>" + phone.replaceAll(";", "<br/>") + "</td>");
			buffer.append("<td>" + email.replaceAll(";", "<br/>") + "</td>");

			String style = "";
			String speedDesc = getSpeedDesc(Long.parseLong(speed), lowerSpeed, midSpeed, goodSpeed);
			if ("慢".equals(speedDesc)) {
				style = " style=\"background:yellow;\"";
			} else if ("差".equals(speedDesc)) {
				style = " style=\"background:red;\"";
			}
			buffer.append("<td" + style + ">" + speedDesc + "(" + speed + "s)</td>");

			if (siteName.length() > 50) {
				siteName = siteName.substring(0, 30);
			}
			buffer.append("<td>" + siteName + "</td>");
			buffer.append("<td>" + registrationNum + "</td>");
			// buffer.append("<td>" + address + "</td>");
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

	private static String getSpeedDesc(long speed, long lowerSpeed, long midSpeed, long goodSpeed) {
		if (speed > midSpeed) {
			if (speed > lowerSpeed) {
				return "差";
			} else {
				return "慢";
			}
		} else {
			if (speed > goodSpeed) {
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
		map.put(INFOKEY_SPEED, speed + "");
		if (speed > LOWEST_SPEED) {
			LOWEST_SPEED = speed;
		}

		String siteName = getFirstMatchItems(content, "<title>([^<>]+)</title>", 1);
		siteName = siteName.replaceAll("\\s", "");
		map.put(INFOKEY_SITENAME, siteName);

		String registrationNum = getFirstMatchItems(content, ".ICP(备|证)\\d+号", 0);
		if (StringUtils.isNotBlank(registrationNum)) {
			map.put(INFOKEY_REGISTRATIONNUM, registrationNum);
		}

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
			logger.info(key + ":" + matchItems);
			if (StringUtils.isNotBlank(matchItems)) {
				map.put(key, matchItems);
			}
		}
	}

	private static String getContactPageUrl(String host, String content) {
		String regex = "<a[^<>]+href=\"([^<>\"]+)\"[^<>]*>\\s*((联系我们)|(联系方式)|(contact us))\\s*</a>";
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
			while ((size = is.read(bytes)) > 0) {
				bos.write(bytes, 0, size);
			}

			String content = new String(bos.toByteArray(), "utf-8").toLowerCase(); // lower case
			try {
				String encode = getFirstMatchItems(content, "<meta\\s+http\\-equiv=\"content\\-type\"\\s+content=\"text/html;\\s+charset=([^<>\" ]+)\"\\s*>", 1);
				if (StringUtils.isNotBlank(encode) && !"utf-8".equalsIgnoreCase(encode) && !"utf8".equalsIgnoreCase(encode)) {
					content = new String(bos.toByteArray(), encode).toLowerCase();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
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
		DefaultHttpClient client = new HaaHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		return client;
	}

}
