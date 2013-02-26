/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:GetBuildingList.java	
 * Created Time:2012-6-19下午11:20:41
 */
package org.haaprojects.website.info.xmhouse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 
 * 獲得樓盤信息：http://www.xmjydj.com/Lp?ProjectId=102835<br>
 * 
 * 活動樓盤在售房信息：http://www.xmjydj.com/Lp/LPPartial <br>
 * 建築編號：BuildID:10130<br>
 * 區編號：NAID:3828<br>
 * lotid:123423<br>
 * t:1361801626250<br>
 * <br>
 * 
 * 活動房產參考價格：http://www.xmjydj.com/House/Fwztxx?HouseId=214563&yyxx=%3Cbr/%
 * 3E%u9762%u79EF%3A89.29&zs=1
 * 
 * 
 * @author Geln Yang
 * 
 */
public class GetBuildingDetailList {

	private static final String HOUSE_ID_HTML_PREFIX = "onclick=\"cellclick(this)\" id=\"";

	private static final String HOUSE_INFO_HTML_PREFIX = "onmouseover=\"showtip('";

	private static final String SPLITOR = "||";

	private static final String NEWLINE = "\r\n";

	static String encoding = "UTF-8";

	public static void main(String[] args) throws IOException {

		InputStream is = new FileInputStream("d:/xmhouse_build_list.txt");

		List<String> lines = IOUtils.readLines(is, encoding);
		HashSet<String> projectIdSet = new HashSet<String>();
		projectIdSet.addAll(lines);

		String outputFilePath = "e:/housedata/";
		String outputFilePrefix = "xmhouse_house_detail_list";
		String outPutFileSuffix = ".txt";
		File outputFile = new File(outputFilePath + outputFilePrefix + outPutFileSuffix);

		if (outputFile.exists()) {
			outputFile.renameTo(new File(outputFilePath + outputFilePrefix + "_" + System.currentTimeMillis() + outPutFileSuffix));
			outputFile = new File(outputFilePath + outputFilePrefix + outPutFileSuffix);
		}

		FileOutputStream fos = new FileOutputStream(outputFile);

		DefaultHttpClient httpClient = new DefaultHttpClient();

		for (String buildInfo : projectIdSet) {
			StringBuffer buffer = new StringBuffer();
			List<String> houseList = getBuildDetailList(httpClient, buildInfo);
			for (String house : houseList) {
				buffer.append(buildInfo + SPLITOR + house + NEWLINE);
				System.out.println(house);
			}
			IOUtils.writeLines(houseList, NEWLINE, fos);
		}
		fos.close();
		httpClient.getConnectionManager().shutdown();

		System.out.println("over");
	}

	public static List<String> getBuildDetailList(DefaultHttpClient httpClient, String buildInfo) throws IOException {
		String[] arr = buildInfo.split(",");
		String BuildID = arr[0];
		String NAID = arr[1];
		String lotid = arr[2];

		List<String> list = new ArrayList<String>();
		String url = "http://www.xmjydj.com/Lp/LPPartial?BuildID=" + BuildID + "&NAID=" + NAID + "&lotid=" + lotid + "&t=" + System.currentTimeMillis();
		HttpPost post = new HttpPost(url);

		// post.addHeader("Accept", "text/html, */*; q=0.01");
		// post.addHeader("Accept-Charset", "UTF-8,*;q=0.5");
		// post.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		// post.addHeader("Accept-Language",
		// "en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4");
		// post.addHeader("Connection", "keep-alive");

		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		// post.addHeader("Host", "www.xmjydj.com");
		// post.addHeader("Origin", "http://www.xmjydj.com");
		// post.addHeader("Referer",
		// "http://www.xmjydj.com/Lp?ProjectId=102835");
		// post.addHeader("Cookie",
		// "ASP.NET_SessionId=rrkv22bl1cg1hrd5hy5x0dvy; DispLp102835=10112%2C3822%2C123423");
		// post.addHeader("User-Agent",
		// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.97 Safari/537.22");
		post.addHeader("X-Requested-With", "XMLHttpRequest");

		// post.addHeader("Content-Length","52");

		// HttpParams params = new BasicHttpParams();
		// params.setParameter("BuildID", BuildID);
		// params.setParameter("NAID", NAID);
		// params.setParameter("lotid", lotid);
		// params.setParameter("t", System.currentTimeMillis());
		// post.setParams(params);

		HttpResponse response = httpClient.execute(post);
		HttpEntity entity = response.getEntity();
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				// throw new IOException("get unexpected status code:" +
				// response.getStatusLine() + "," + url);
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

			StringBuffer buffer = new StringBuffer(content);
			int endIndex = 0;
			int startIndex = buffer.indexOf(HOUSE_ID_HTML_PREFIX, 0);
			while (startIndex > 0) {
				startIndex += HOUSE_ID_HTML_PREFIX.length();
				endIndex = buffer.indexOf("\"", startIndex);
				if (endIndex <= startIndex) {
					break;
				}
				String id = buffer.substring(startIndex, endIndex);

				startIndex = buffer.indexOf(HOUSE_INFO_HTML_PREFIX, endIndex);
				startIndex += HOUSE_INFO_HTML_PREFIX.length();
				endIndex = buffer.indexOf("')\"", startIndex);
				if (endIndex <= startIndex) {
					break;
				}
				String info = buffer.substring(startIndex, endIndex);
				info = info.replace("&lt;strong&gt;", "").replace("&lt;/strong&gt;", "").replace("&lt;br/&gt;", "||");

				int price = getHousePrice(httpClient, id);

				list.add(id + SPLITOR + info + SPLITOR + "价格:" + price);

				// NEXT
				startIndex = buffer.indexOf(HOUSE_ID_HTML_PREFIX, endIndex);

			}

			// System.out.println(content);
		} finally {
			EntityUtils.consume(entity);
		}
		return list;
	}

	/**
	 * @param httpClient
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws
	 */
	private static int getHousePrice(DefaultHttpClient httpClient, String id) throws IOException {
		String url = "http://www.xmjydj.com/House/Fwztxx?HouseId=" + id + "&yyxx=%3Cbr/%3E面积&zs=1";

		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				// throw new IOException("get unexpected status code:" +
				// response.getStatusLine() + "," + url);
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

			String prefix = "拟售单价:";
			int start = content.indexOf(prefix);
			if (start > 0) {
				start += prefix.length();
				int end = content.indexOf("<", start);
				if (end > start) {
					return Integer.valueOf(content.substring(start, end));
				}
			}

		} finally {
			EntityUtils.consume(entity);
		}
		return 0;

	}
}
