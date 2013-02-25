/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:GetProjectList.java	
 * Created Time:2012-6-19下午11:20:41
 */
package org.haaprojects.website.info.xmhouse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * 獲得樓盤列表：http://www.xmjydj.com/SEARCH/PROJECTLISTPARITAL?IDX=1&PAGE=3
 * Post parameters:kfs: xmmc: ysxkz: xmdz: xmdq: fwyt:undefined
 * 
 * @author Geln Yang
 * 
 */
public class GetProjectList {


	public static void main(String[] args) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		List<String> projectIdList = getProjectIdList(httpClient);
		System.out.println("==========================");
		for (String projectId : projectIdList) {
			System.out.println(projectId);
		}
		httpClient.getConnectionManager().shutdown();
		System.out.println("over");
	}

	private static List<String> getProjectIdList(DefaultHttpClient httpClient) throws IOException {
		List<String> list = new ArrayList<String>();
		int currentPageNo = 0;
		int lastPageNo = 1;

		while (currentPageNo < lastPageNo) {

			currentPageNo++;

			String url = "http://www.xmjydj.com/SEARCH/PROJECTLISTPARITAL?IDX=1&PAGE=" + currentPageNo;
			HttpPost post = new HttpPost(url);
			HttpParams params = new BasicHttpParams();
			params.setParameter("kfs", "");
			params.setParameter("xmmc", "");
			params.setParameter("ysxkz", "");
			params.setParameter("xmdz", "");
			params.setParameter("xmdq", "");
			params.setParameter("fwyt", "undefined");
			post.setParams(params);

			post.addHeader("X-Requested-With", "XMLHttpRequest");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");

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
				String content = new String(htmlByts).toLowerCase(); // lower
																		// case
				System.out.println(content);

				Pattern pattern = Pattern.compile("href=\"/houseproject/detail\\?xmlsh=(\\d+)&");
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					String xmlsh = matcher.group(1);
					System.out.println(xmlsh);
					list.add(xmlsh);
				}

				Pattern pagenoPattern = Pattern.compile("/search/projectlistparital\\?idx=1&amp;page=(\\d+)");
				Matcher pagenoMatcher = pagenoPattern.matcher(content);
				while (pagenoMatcher.find()) {
					String no = pagenoMatcher.group(1);
					int pageNo = Integer.parseInt(no);
					if (pageNo > lastPageNo) {
						lastPageNo = pageNo;
					}
				}
			} finally {
				EntityUtils.consume(entity);
			}
		}
		return list;
	}

}
