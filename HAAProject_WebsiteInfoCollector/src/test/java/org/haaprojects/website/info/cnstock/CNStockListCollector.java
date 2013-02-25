/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:CNStockListCollector.java	
 * Created Time:2012-6-19下午11:20:41
 */
package org.haaprojects.website.info.cnstock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.haaprojects.site.collector.RegexUtil;

/**
 * @author Geln Yang
 * 
 */
public class CNStockListCollector {

	static String baseHost = "http://chinext.cnstock.com";
	private static String regex = "<tr><td[^>]+>([^<>]+)</td><td[^>]+>([^\r\n]+)</td></tr>";
	static Pattern pattern = Pattern.compile(regex);
	private static String encoding="UTF-8";

	public static void main(String[] args) throws IOException {
		InputStream is = CNStockListCollector.class.getResourceAsStream("/org/haaprojects/website/info/cnstocklist.txt");
		List<String> lines = IOUtils.readLines(is, encoding);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		for (String line : lines) {
			if (StringUtils.isBlank(line)) {
				continue;
			}
			String url = baseHost + "/" + line;
			String content = getUrlContent(httpClient, url);
			addCompanyData(dataList, content);
		}

		if (dataList.size() > 0) {
			StringBuffer outputBuffer = new StringBuffer();
			outputBuffer.append("<table border=1><tr>");
			Map<String, String> map = dataList.get(0);
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				outputBuffer.append("<th>" + key + "</th>");
			}
			outputBuffer.append("</tr>");

			for (Map<String, String> item : dataList) {
				outputBuffer.append("<tr>");
				for (String key : keySet) {
					outputBuffer.append("<td>" + item.get(key) + "</td>");
				}
				outputBuffer.append("</tr>");
			}

			FileUtils.writeStringToFile(new File("d:/stock_list.html"), outputBuffer.toString(), "UTF-8");
			System.out.println("over");
		}

		httpClient.getConnectionManager().shutdown();
		System.out.println("over");
	}

	/**
	 * @param dataList
	 * @param content
	 */
	private static void addCompanyData(List<Map<String, String>> dataList, String content) {
		String startFlag = "class=\"greytable\">";
		String endFlag = "</table>";
		int start = content.indexOf(startFlag);
		if (start > 0) {
			start += startFlag.length();
			int end = content.indexOf(endFlag, start);
			String tableContent = content.substring(start, end);
			Matcher matcher = pattern.matcher(tableContent);

			Map<String, String> map = new LinkedHashMap<String, String>();
			dataList.add(map);
			while (matcher.find()) {
				String key = matcher.group(1);
				key = key.replace(":", "");
				String val = matcher.group(2);
				map.put(key, val);
			}
			System.out.println(map);

		}

	}

	public static String getUrlContent(DefaultHttpClient httpClient, String url) throws IOException {
		HttpGet request = new HttpGet(url);
		request.addHeader("Accept-Charset", "utf-8");
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				// throw new IOException("get unexpected status code:" + response.getStatusLine() + "," + url);
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
			bos.close();
			String content = new String(htmlByts).toLowerCase(); // lower case
			try {
				String encode = RegexUtil.getFirstMatchItems(content, "<meta\\s+http\\-equiv=\"content\\-type\"\\s+content=\"text/html;\\s+charset=([^<>\" ]+)\"", 1);
				if (StringUtils.isNotBlank(encode)) {
					content = new String(htmlByts, encode).toLowerCase();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return content;
		} finally {
			EntityUtils.consume(entity);
		}
	}
}
