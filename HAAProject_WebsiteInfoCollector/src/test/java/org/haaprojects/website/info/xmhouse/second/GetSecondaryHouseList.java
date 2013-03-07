/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:GetBuildingList.java	
 * Created Time:2012-6-19下午11:20:41
 */
package org.haaprojects.website.info.xmhouse.second;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.haaprojects.website.info.xmhouse.HouseDAO;
import org.haaprojects.website.info.xmhouse.domain.SecondaryHouse;

/**
 * 
 * 1. http://www.xmjydj.com/search/house?idx=5&hasBanner=true&page=1<br>
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
public class GetSecondaryHouseList {

	static String encoding = "UTF-8";

	public static void main(String[] args) throws Exception {
		Connection connection = HouseDAO.getJDBCConnection();
		DefaultHttpClient httpClient = new DefaultHttpClient();

		fetchHouseDetail(httpClient, connection);

		httpClient.getConnectionManager().shutdown();
		connection.close();

		System.out.println("over");
	}

	public static List<String> fetchHouseDetail(DefaultHttpClient httpClient, Connection connection) throws IOException {
		List<String> list = new ArrayList<String>();
		int currentPageNo = 0;
		int lastPageNo = 1;

		while (currentPageNo < lastPageNo) {

			currentPageNo++;

			String url = "http://www.xmjydj.com/search/HouseListParital?idx=5&hasBanner=true&page=" + currentPageNo;
			HttpPost post = new HttpPost(url);
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

				Pattern pattern = Pattern.compile("/house/detail\\?fyxh=(\\d+)&amp;idx=5");
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					String houseId = matcher.group(1);
					System.out.println(houseId);
					parseHouseDetail(connection, httpClient, houseId);

				}

				// get the max pageno
				Pattern pagenoPattern = Pattern.compile("/search/HouseListParital\\?idx=5&amp;hasbanner=true&amp;page=(\\d+)",Pattern.CASE_INSENSITIVE);
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

	/**
	 * @param connection
	 * @param httpClient
	 * @param houseId
	 * @param link
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private static void parseHouseDetail(Connection connection, AbstractHttpClient httpClient, String houseId) throws ClientProtocolException, IOException {
		SecondaryHouse houseDetail = new SecondaryHouse();
		houseDetail.setId(houseId);

		String url = "http://www.xmjydj.com/House/Detail?fyxh=" + houseId + "&idx=5";

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
			String c = new String(htmlByts).toLowerCase(); // lower case
			StringBuffer content = new StringBuffer(c);

			String prefix = "<table class=\"tab1\" id=\"roomdetail";
			int tableStart = content.indexOf(prefix);
			if (tableStart <= 0) {
				return;
			}
			int tableEnd = content.indexOf("</table>", tableStart + prefix.length());
			int start = tableStart;
			int end = tableStart + prefix.length();

			while (start > 0 && start < tableEnd) {
				start = content.indexOf("<td", end);
				start = content.indexOf(">", start);
				start++;
				end = content.indexOf("</td", start);

				String key = content.substring(start, end).trim();

				start = content.indexOf("<td", end);
				start = content.indexOf(">", start);

				end = content.indexOf("</td", start);
				if (end <= start) {
					return;
				}

				String value = content.substring(start + 1, end).trim();
				value = checkValue(value);

				houseDetail.setFieldValue(key, value);
			}

			tableStart = content.indexOf("<table class=\"tab1\"", tableEnd);
			tableStart = content.indexOf("<tbody", tableStart);

			tableEnd = content.indexOf("</tbody>", tableStart);

			start = content.indexOf("<td", tableStart);
			start = content.indexOf(">", start);
			end = content.indexOf("</td>", start);
			String companyName = content.substring(start + 1, end);

			start = content.indexOf("<td", end);
			start = content.indexOf(">", start);
			end = content.indexOf("</td>", start);
			String contact = content.substring(start + 1, end);

			start = content.indexOf("<td", end);
			start = content.indexOf(">", start);
			end = content.indexOf("</td>", start);
			String publishTime = content.substring(start + 1, end);

			start = content.indexOf("<td", end);
			start = content.indexOf(">", start);
			end = content.indexOf("</td>", start);
			String phoneno = content.substring(start + 1, end);

			start = content.indexOf("<td", end);
			start = content.indexOf(">", start);
			end = content.indexOf("</td>", start);
			String qq = content.substring(start + 1, end);

			start = content.indexOf("<td", end);
			start = content.indexOf(">", start);
			end = content.indexOf("</td>", start);
			String email = content.substring(start + 1, end);

			houseDetail.setCompanyName(checkValue(companyName));
			houseDetail.setContact(checkValue(contact));
			houseDetail.setPublishTime(checkValue(publishTime));
			houseDetail.setPhoneNo(checkValue(phoneno));
			houseDetail.setQq(checkValue(qq));
			houseDetail.setEmail(checkValue(email));

			HouseDAO.saveSecondaryHouse(connection, houseDetail);

		} finally {
			EntityUtils.consume(entity);
		}

	}

	/**
	 * @param value
	 * @return
	 */
	private static String checkValue(String value) {
		if (value != null) {
			value = value.trim().toLowerCase();
			if (value.startsWith("<")) {
				int start = value.indexOf(">", 2);
				if (start > 0) {
					int end = value.indexOf("</", start + 1);
					if (end > start) {
						value = value.substring(start + 1, end);
						if (value.startsWith("<") && value.endsWith(">")) {
							value = null;
						}
					}
				}
			}
		}
		return value;
	}
}
