/**
 * ====================================================<br>
 * File: OAuthServlet.java<br>
 * Create:May 26, 2011<br>
 * Author:Geln Yang<br>
 * ====================================================<br>
 * <!-- comment -->
 * 
 */
package org.haaprojects.oauth2.servlet;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c4j.net.NetTools;

/**
 * <pre><b><font color="blue">OAuthServlet</font></b></pre>
 * 
 * <pre><b>&nbsp;-- description--</b></pre>
 * <pre></pre>
 * 
 * JDK Version：1.6
 * 
 * @author <b>Geln Yang</b>
 */
public class OAuthServlet extends HttpServlet {

	private static final Log logger = LogFactory.getLog(OAuthServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//getAuthorizationCode(resp);
		getAccessCode(resp);
	}

	/**
	 */
	public void getAccessCode(HttpServletResponse resp) throws IOException {
		String url = "https://accounts.google.com/o/oauth2/token";
		Map<String, String> params = new HashMap<String, String>();
		params.put("client_id", "619519131567.apps.googleusercontent.com");
		params.put("redirect_uri", "http://sisopipo.com/oauth2/callback");
		params.put("client_secret", "NID8Vq9xMsEu6h_g0aq0qvO_");
		params.put("grant_type", "authorization_code");
		params.put("code", "4/1KPIN60PMdH1uE7tzNF3Nwkg-0Ak");
		String paramString = buildParamString(params);
		Reader reader = new StringReader(paramString);
		try {
			NetTools.postData(reader, new URL(url), resp.getWriter(), "application/x-www-form-urlencoded");
			logger.info(resp.getWriter().toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resp.getWriter().append(e.getMessage());
		}

	}

	//
	public void getAuthorizationCode(HttpServletResponse resp) throws IOException {
		String url = "https://accounts.google.com/o/oauth2/auth";
		Map<String, String> params = new HashMap<String, String>();
		params.put("client_id", "619519131567.apps.googleusercontent.com");
		params.put("redirect_uri", "http://sisopipo.com/oauth2/callback");
		params.put("scope", "https://www.google.com/m8/feeds/");
		params.put("response_type", "code");
		resp.setContentType("application/x-www-form-urlencoded");
		resp.sendRedirect(url + "?" + buildParamString(params));
	}

	private String buildParamString(Map<String, String> params) throws IOException {
		StringBuffer buffer = new StringBuffer();
		if (params != null) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				String val = params.get(key);
				buffer.append(key + "=" + val + "&");
			}
		}
		return buffer.toString();
	}
}
