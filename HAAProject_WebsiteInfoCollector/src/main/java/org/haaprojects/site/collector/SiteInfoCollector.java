package org.haaprojects.site.collector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.haaprojects.http.HaaHttpClient;
import org.haaprojects.site.collector.data.CollectData;
import org.haaprojects.site.collector.data.SiteInfo;
import org.haaprojects.site.collector.generator.HtmlGenerator;

/**
 * Site Info Collector
 */
public class SiteInfoCollector {

	private static final Log logger = LogFactory.getLog(SiteInfoCollector.class);

	public static final int THREAD_SIZE = 5;

	public static long maxLoadTime = 0; // inital to the min
	public static long minLoadTime = 100; // inital to the max

	static String running_thread_lock = "LOCK";
	static int running_thread_count = 0;
	static int totalHostSize = 0;
	static int processedHostSize = 0;

	interface HostContainer {
		public String getNextHost();
	}

	public static void main(String[] args) throws Exception {
		final CollectData dataMap = new CollectData();
		String filePath = args[0];
		logger.info("Site source file:" + filePath);

		final File file = new File(filePath);
		final List<String> lines = FileUtils.readLines(file, "UTF-8");
		Set<String> siteSet = Collections.synchronizedSet(new HashSet<String>());
		for (String siteinfo : lines) {
			siteinfo = siteinfo.toLowerCase();
			String host = getHost(siteinfo);
			if (StringUtils.isNotBlank(host)) {
				siteSet.add(host);
			}
		}

		totalHostSize = siteSet.size();

		final Iterator<String> siteIterator = siteSet.iterator();
		final HostContainer hostContainer = new HostContainer() {
			public synchronized String getNextHost() {
				while (siteIterator.hasNext()) {
					processedHostSize++;
					return siteIterator.next();
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
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
					}
					logger.info("-----> threadNum:" + running_thread_count + ",HostSize:" + totalHostSize + ",ProcessedHostSize:" + processedHostSize);
					synchronized (running_thread_lock) {
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

				logger.info("==============>>> over");
			}

		}.start();

	}

	private static void writeResult(File file, CollectData dataMap) throws IOException {
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String baseName = FilenameUtils.getBaseName(file.getName());
		String dir = file.getParent();
		CharSequence content = new HtmlGenerator().generate(dataMap);
		FileUtils.write(new File(dir + baseName + "_output_" + time + ".html"), content, "UTF-8");
	}

	private static void collectSiteInfo(DefaultHttpClient httpClient, CollectData dataMap, String host) throws IOException {
		logger.info("----> parsing:" + host);
		SiteInfo siteInfo = dataMap.getSiteInfo(host);

		long start = System.currentTimeMillis();
		String content = getUrlContent(httpClient, host);
		long end = System.currentTimeMillis();

		long speed = (end - start) / 1000;
		siteInfo.put(Const.INFOKEY_LOADTIME, speed + "");
		if (speed > maxLoadTime) {
			maxLoadTime = speed;
		}
		if (speed < minLoadTime) {
			minLoadTime = speed;
		}
		String siteName = getFirstMatchItemsByInfokey(content, Const.INFOKEY_TITLE);
		String registrationNum = getFirstMatchItemsByInfokey(content, Const.INFOKEY_REGISTRATIONNUM);
		String aboutUsUrl = getFirstMatchItemsByInfokey(content, Const.INFOKEY_ABOUNTUSURL);
		aboutUsUrl = correctSubUrl(host, aboutUsUrl);
		String qq = getFirstMatchItemsByInfokey(content, Const.INFOKEY_QQ);

		String contactPageUrl = getContactPageUrl(host, content);

		if (StringUtils.isNotBlank(contactPageUrl)) {
			content = getUrlContent(httpClient, contactPageUrl);
			siteInfo.put(Const.INFOKEY_CONTACEPAGEURL, contactPageUrl);
			if (StringUtils.isBlank(siteName)) {
				siteName = getFirstMatchItemsByInfokey(content, Const.INFOKEY_TITLE);
			}
			if (StringUtils.isBlank(registrationNum)) {
				registrationNum = getFirstMatchItemsByInfokey(content, Const.INFOKEY_REGISTRATIONNUM);
			}
			if (StringUtils.isBlank(aboutUsUrl)) {
				aboutUsUrl = getFirstMatchItemsByInfokey(content, Const.INFOKEY_ABOUNTUSURL);
			}
			if (StringUtils.isBlank(qq)) {
				qq = getFirstMatchItemsByInfokey(content, Const.INFOKEY_QQ);
			}
		}

		if (StringUtils.isNotBlank(siteName)) {
			siteName = siteName.replaceAll("\\s", "");
			siteInfo.put(Const.INFOKEY_SITENAME, siteName);
		}
		if (StringUtils.isNotBlank(registrationNum)) {
			siteInfo.put(Const.INFOKEY_REGISTRATIONNUM, registrationNum);
		}
		if (StringUtils.isNotBlank(aboutUsUrl)) {
			siteInfo.put(Const.INFOKEY_ABOUNTUSURL, aboutUsUrl);
		}

		String phones = getMatchItemsByInfokey(content, Const.INFOKEY_PHONE);
		if (StringUtils.isNotBlank(phones)) {
			siteInfo.put(Const.INFOKEY_PHONE, phones);
		}
		String emails = getMatchItemsByInfokey(content, Const.INFOKEY_EMAIL);
		if (StringUtils.isNotBlank(emails)) {
			siteInfo.put(Const.INFOKEY_EMAIL, emails);
		}

		logger.info(host + ":" + siteInfo);
	}

	private static String getUrlContent(DefaultHttpClient httpClient, String host) throws IOException {
		String content = HttpUtil.getUrlContent(httpClient, host);
		if (content != null) {
			/* convert the html blank and let it easy for regex matching */
			content = content.replace("&nbsp;", " ");
		}
		return content;
	}

	private static String getHost(String siteinfo) {
		String host = RegexUtil.getMatchItems(siteinfo, "\\s*(([a-zA-Z0-9\\-]+\\.)+(com|cn))\\s*", 1);
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

	private static String getContactPageUrl(String host, String content) {
		String contactPageUrl = getFirstMatchItemsByInfokey(content, Const.INFOKEY_CONTACEPAGEURL);

		if (StringUtils.isBlank(contactPageUrl)) {
			contactPageUrl = RegexUtil.getFirstMatchItems(content,
					"<a[^<>]+href=\"((http://)?(([a-z0-9\\-]+\\.)+[a-z]{1,5}(:\\d{2,6})?)?(/[a-z0-9_\\-]+)*[/]?contact([_]?us)?\\.[a-z]{3,5})\"[^<>]*>\\s*[^<>\\s]+\\s*</a>", 1);
		}
		if (StringUtils.isBlank(contactPageUrl)) {
			String regex = "window\\.location\\.href=([^;=\\s]+)";
			contactPageUrl = RegexUtil.getFirstMatchItems(content, regex, 1);
			if (StringUtils.isNotBlank(contactPageUrl)) {
				contactPageUrl = contactPageUrl.replace("\"", "").replace("\'", "");
				/* whether the script value is a link */
				if (!contactPageUrl.startsWith("/") && !contactPageUrl.startsWith("http")) {
					contactPageUrl = null;
				}
			}
		}

		return correctSubUrl(host, contactPageUrl);
	}

	private static String correctSubUrl(String host, String subUrl) {
		if (StringUtils.isNotBlank(subUrl)) {
			if (subUrl.startsWith("http")) {
				return subUrl;
			} else {
				if (subUrl.startsWith("/")) {
					return host + subUrl;
				} else if (subUrl.startsWith("mailto:")) {
					return null;
				} else {
					return host + "/" + subUrl;
				}
			}
		}
		return null;
	}

	private static String getFirstMatchItemsByInfokey(String content, String infoKey) {
		try {
			return RegexUtil.getFirstMatchItems(content, RegexConfig.getRegex(infoKey), RegexConfig.getRegexGroupNum(infoKey));
		} catch (Exception e) {
			return "";
		}
	}

	private static String getMatchItemsByInfokey(String content, String infoKey) {
		try {
			return RegexUtil.getMatchItems(content, RegexConfig.getRegex(infoKey), RegexConfig.getRegexGroupNum(infoKey));
		} catch (Exception e) {
			return "";
		}
	}

	private static DefaultHttpClient createHttpClient() {
		return new HaaHttpClient();
	}

}
