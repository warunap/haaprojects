/**
 *      Project:HAAProject_WebsiteInfoCollector       
 *    File Name:GetBuildingList.java	
 * Created Time:2012-6-19下午11:20:41
 */
package org.haaprojects.website.info.xmhouse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
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

	private static final String NEWLINE = "\r\n";

	private static String encoding = "UTF-8";

	public static void main(String[] args) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.setRedirectStrategy(new DefaultRedirectStrategy() {

			public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
				boolean isRedirect = false;
				try {
					isRedirect = super.isRedirected(request, response, context);
				} catch (ProtocolException e) {
					e.printStackTrace();
				}
				if (!isRedirect) {
					int responseCode = response.getStatusLine().getStatusCode();
					if (responseCode == 301 || responseCode == 302) {
						return true;
					}
				}
				return isRedirect;
			}
		});

//		CookieStore cookieStore = new BasicCookieStore();
//		httpClient.setCookieStore(cookieStore);
//
//		Cookie cookie = new BasicClientCookie("DispLp102835", "10112,3822,123423");
//		cookieStore.addCookie(cookie);
//		HttpGet httpGet = new HttpGet("http://www.xmjydj.com/Lp?ProjectId=102835");
//		HttpResponse response = httpClient.execute(httpGet);
//		InputStream is = response.getEntity().getContent();
//		List<String> lines = IOUtils.readLines(is);
//		for (String string : lines) {
//			System.out.println(string);
//		}

		getBuildDetailList(httpClient, "10112,3822,123423");

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

			System.out.println(content);
		} finally {
			EntityUtils.consume(entity);
		}
		return list;
	}
}
