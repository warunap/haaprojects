/**
 * Created By: Geln Yang
 * Created Date: 2013-4-2 上午11:38:13
 */
package org.haaproject.kerberos.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jcifs.Config;
import jcifs.UniAddress;
import jcifs.http.AuthenticationFilter;
import jcifs.netbios.NbtAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaproject.kerberos.servlet.LogWrapperHttpResponse;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class KerberosAuthenticationFilter extends AuthenticationFilter {

	private static final Log logger = LogFactory.getLog(KerberosAuthenticationFilter.class);

	private String[] escapedUrls;

	public void init(FilterConfig filterConfig) throws ServletException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kerberos/SSOConfig.properties");
		if (is == null)
			logger.error("Can't find single sign one configuration file(SSOConfig.properties)!");
		else {
			try {
				Properties prop = new Properties();
				prop.load(is);
				is.close();
				System.getProperties().putAll(prop);

				Set<Object> keySet = prop.keySet();
				for (Object key : keySet) {
					String k = (String) key;
					String val = prop.getProperty(k);
					logger.debug(k + "\t=\t" + val);
					if (k.startsWith("jcifs")) {
						Config.setProperty(k, val);
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		super.init(filterConfig);

		String urlParam = filterConfig.getInitParameter("escapedUrls");
		if (urlParam == null || urlParam.trim().length() == 0)
			escapedUrls = new String[] {};
		else
			escapedUrls = urlParam.split(",");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String url = req.getRequestURL().toString();
		logger.debug("request url: " + url);

		for (String escapedUrl : escapedUrls) {
			if (url.endsWith(escapedUrl)) {
				logger.info("escape SSO authorization, url: " + url);
				chain.doFilter(request, response);
				return;
			}
		}

		HttpSession session = req.getSession();
		if (session != null) {
			String userId = (String) session.getAttribute("LOGIN_USER_ID");
			if (userId != null && userId.trim().length() > 0) {
				chain.doFilter(request, response);
				return;
			}
		}

		logger.info("need to do super filter operation, url: " + url);
		UniAddress address = getDomainController();
		System.out.println(address);
		System.out.println("=================================");
		System.out.println("request header");
		System.out.println("--------------");
		@SuppressWarnings("unchecked")
		Enumeration<String> headerNames = req.getHeaderNames();
		for (; headerNames.hasMoreElements();) {
			String name = headerNames.nextElement();
			String val = req.getHeader(name);
			System.out.println(name + "\t\t=\t" + val);
		}

		System.out.println("####");
		System.out.println("response header");
		System.out.println("--------------");
		LogWrapperHttpResponse resp = new LogWrapperHttpResponse((HttpServletResponse) response);
		super.doFilter(request, resp, chain);
		System.out.println("=================================");

	}

	private UniAddress getDomainController() throws UnknownHostException {
		boolean loadBalance = false;
		String domainController = getProperty("jcifs.http.domainController");
		if (domainController == null) {
			domainController = getProperty("jcifs.smb.client.domain");
			String balance = getProperty("jcifs.http.loadBalance");
			loadBalance = (balance != null) ? Boolean.valueOf(balance).booleanValue() : true;

		}
		return loadBalance ? new UniAddress(NbtAddress.getByName(domainController, 0x1c, null)) : UniAddress.getByName(domainController, true);
	}

	public String getProperty(String property) {
		String value = jcifs.Config.getProperty(property);
		if (value == null)
			value = Config.getProperty(property);
		return (value != null) ? value : System.getProperty(property);
	}
}
