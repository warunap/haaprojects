/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:GetBuildingList.java	
 * Created Time:2012-6-19下午11:20:41
 */
package org.haaprojects.website.info.xmhouse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.haaprojects.website.info.cnstock.CNStockListCollector;

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
public class GetBuildingList {

	private static final String NEWLINE = "\r\n";

	private static String encoding = "UTF-8";

	public static void main(String[] args) throws IOException {
		InputStream is = CNStockListCollector.class.getResourceAsStream("/org/haaprojects/website/info/xmhouse/projectlist.txt");
		List<String> lines = IOUtils.readLines(is, encoding);
		HashSet<String> projectIdSet = new HashSet<String>();
		projectIdSet.addAll(lines);

		String outputFilePath = "d:/";
		String outputFilePrefix = "xmhouse_build_list";
		String outPutFileSuffix = ".txt";
		File outputFile = new File(outputFilePath + outputFilePrefix + outPutFileSuffix);

		if (outputFile.exists()) {
			outputFile.renameTo(new File(outputFilePath + outputFilePrefix + "_" + System.currentTimeMillis() + outPutFileSuffix));
			outputFile = new File(outputFilePath + outputFilePrefix + outPutFileSuffix);
		}
		
		DefaultHttpClient httpClient = new DefaultHttpClient();

		for (String projectId : projectIdSet) {
			StringBuffer buffer = new StringBuffer();
			List<String> buildList = getBuildList(httpClient, projectId);
			for (String build : buildList) {
				buffer.append(build + NEWLINE);
				System.out.println(build);
			}
			FileUtils.writeStringToFile(outputFile, buffer.toString(), encoding);
		}

		httpClient.getConnectionManager().shutdown();

		System.out.println("over");
	}

	public static List<String> getBuildList(DefaultHttpClient httpClient, String projectId) throws IOException {
		List<String> list = new ArrayList<String>();
		HttpGet request = new HttpGet("http://www.xmjydj.com/Lp?ProjectId=" + projectId);
		request.addHeader("Accept-Charset", "utf-8");
		HttpResponse response = httpClient.execute(request);
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

			Pattern pattern = Pattern.compile("href=\"javascript:displp\\((\\-?\\d+,\\-?\\d+,\\-?\\d+)\\)");
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				String buildInfo = matcher.group(1);
				list.add(buildInfo);
			}

		} finally {
			EntityUtils.consume(entity);
		}
		return list;
	}
}
