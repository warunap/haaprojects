/**
 * Created Date: May 24, 2012 4:04:24 PM
 */
package org.haaprojects.http;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class HaaHttpClient extends DefaultHttpClient {

	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds
	public static final int SOCKET_TIMEOUT = 50 * 1000; // milliseconds
	private static final String DEFAULT_CHARSET = "UTF-8";

	public HaaHttpClient() {
	}

	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		registry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		
		HttpParams params = new BasicHttpParams();
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, HTTP_TIMEOUT);
		params.setParameter(HttpConnectionParams.SO_TIMEOUT, SOCKET_TIMEOUT);
		params.setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, DEFAULT_CHARSET);
		
		return new BasicClientConnectionManager(registry);
	}
}
