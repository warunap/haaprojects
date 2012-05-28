/**
 * Created By: Comwave Project Team
 * Created Date: May 28, 2012 6:27:03 PM
 */
package org.haaprojects.site.collector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class HttpUtil {

	private static final Log logger = LogFactory.getLog(HttpUtil.class);

	public static String getUrlContent(DefaultHttpClient httpClient, String host) throws IOException {
		HttpGet request = new HttpGet(host);
		request.addHeader("Accept-Charset", "utf-8;GBK;gb2312");
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
			bos.close();
			String content = new String(htmlByts).toLowerCase(); // lower case
			try {
				String encode = RegexUtil.getFirstMatchItems(content, "<meta\\s+http\\-equiv=\"content\\-type\"\\s+content=\"text/html;\\s+charset=([^<>\" ]+)\"", 1);
				if (StringUtils.isNotBlank(encode)) {
					content = new String(content.getBytes(), encode).toLowerCase();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return content;
		} finally {
			EntityUtils.consume(entity);
		}
	}
}
