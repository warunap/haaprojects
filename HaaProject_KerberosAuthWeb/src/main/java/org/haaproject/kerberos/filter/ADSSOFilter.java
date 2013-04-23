/**
 */
package org.haaproject.kerberos.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ad.sso.SSOHandler;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaproject.kerberos.util.FreeMarkerTemplateManager;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 */

public class ADSSOFilter implements Filter {

	private static final String DEFAULT_ENCODING_CHARSET = "utf-8";

	private static final Pattern MSIE_PATTERN = Pattern.compile("MSIE", Pattern.CASE_INSENSITIVE);

	private static final Pattern WINDOW_PATTERN = Pattern.compile("Windows", Pattern.CASE_INSENSITIVE);

	private static final String USER_AGENT_HEAD = "user-agent";

	private static final String AUTHORIZATION_HEAD = "Authorization";

	private static final String WWW_AUTHENTICATE_HEAD = "WWW-Authenticate";

	private final static String NEG_TOKEN = "Negotiate";

	private final static byte[] OID_NTLM = new byte[] { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00 };

	private final static Log logger = LogFactory.getLog(ADSSOFilter.class);

	private static final String LOING_USER_ID = "LOGIN_USER_ID";

	private static final String ORIGINAL_URL = "ORIGINAL_URL";

	public void destroy() {
	}

	private Template template = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		FreeMarkerTemplateManager templateManager = new FreeMarkerTemplateManager("ftl/");
		try {
			template = templateManager.getTemplate("ssologin.ftl");
		} catch (IOException e) {
			logger.error(e);
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream("kerberos/SSOConfig.properties");
		if (is == null) {
			logger.error("kerberos/SSOConfig.properties file cannot find in classpath.");
		} else {
			Properties props = new Properties();
			try {
				props.load(is);
				initSSOSystemProperties(props);
			} catch (IOException e) {
				logger.error("failed to load kerberos/SSOConfig.properties.");
			}
		}
	}

	private void initSSOSystemProperties(Properties props) {
		String loginConfig = props.getProperty("java.security.auth.login.config");
		if (loginConfig == null || loginConfig.trim().length() == 0) {
			logger.error("java.security.auth.login.config  not set in SSOConfig.properties.");
		} else {
			System.setProperty("java.security.auth.login.config", loginConfig);
		}

		String krbconf = props.getProperty("java.security.krb5.conf");
		if (krbconf == null || krbconf.trim().length() == 0) {
			logger.error("java.security.krb5.conf  not set in SSOConfig.properties.");
		} else {
			System.setProperty("java.security.krb5.conf", krbconf);
		}

		String debug = props.getProperty("sun.security.krb5.debug");
		if (debug == null || debug.trim().length() == 0) {
			logger.warn("sun.security.krb5.debug  not set in SSOConfig.properties.");
		} else {
			System.setProperty("sun.security.krb5.debug", debug);
		}

		String jgss_native = props.getProperty("sun.security.jgss.native");
		if (jgss_native == null || jgss_native.trim().length() == 0) {
			logger.warn("sun.security.jgss.native not set in SSOConfig.properties.");
		} else {
			System.setProperty("sun.security.jgss.native", jgss_native);
		}

		String password = props.getProperty("login.password");
		SSOHandler.setPassword(password);
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String requestURI = request.getRequestURI();
		System.out.println(requestURI);

		if (isLogined(request)) {
			chain.doFilter(req, res);
			return;
		}

		String authorization = request.getHeader(AUTHORIZATION_HEAD);
		logger.debug(AUTHORIZATION_HEAD + ":" + authorization);
		if (authorization != null) {
			if (authorization.startsWith("Basic ")) {
				byte[] bytes = Base64.decodeBase64(authorization.substring(6));
				String authorizationSource = new String(bytes);
				logger.debug(authorizationSource);
				String[] arr = authorizationSource.split(":");
				if (arr.length < 2) {
					requestBasicAuth(request, response);
					return;
				}
				String userId = arr[0];
				String password = arr[1];

				// TODO to validate the user
				request.getSession().setAttribute(LOING_USER_ID, userId);
				chain.doFilter(request, response);
				return;
			} else if (!authorization.startsWith(NEG_TOKEN)) {
				authorization = null;
			}
		}

		if (authorization == null) {
			if (isMSIE(request)) {
				requireSSOToken(response);
				return;
			} else {
				requestBasicAuth(request, response);
				return;
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("receive spnego token:[" + authorization + "]");
			}
			String challenge = authorization.substring(NEG_TOKEN.length() + 1);
			byte[] token = Base64.decodeBase64(challenge.getBytes());
			if (isNTLM(token)) {
				logger.debug("expect kerberos spnego token ,but receive NTLM token[" + authorization + "].");
				logger.debug("system cannot support NTLM login,so your should login manual.");
				requestBasicAuth(request, response);
				return;
			} else {
				String fromName = authenticate(token, response);
				logger.debug("fromName:" + fromName);
				if (fromName == null || fromName.trim().length() == 0) {
					requestBasicAuth(request, response);
					return;
				} else {
					String accountName = retriveAccountName(fromName);
					logger.debug("accountName:" + accountName);
					chain.doFilter(req, res);
					return;
				}
			}
		}
	}

	private void requestBasicAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader(WWW_AUTHENTICATE_HEAD, "Basic");
		if (logger.isDebugEnabled()) {
			logger.debug("Sending response token [" + NEG_TOKEN + "]");
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private String retriveAccountName(String fromName) {
		int index = fromName.indexOf('@');
		if (index >= 0) {
			return fromName.substring(0, index);
		}
		return fromName;
	}

	private void processManualLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		setOriginalUrl(request);
		response.setCharacterEncoding(DEFAULT_ENCODING_CHARSET);
		forbidCache(response);
		// response.getWriter().println(buildSSOLoginPage(request));
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private void setOriginalUrl(HttpServletRequest request) {
		request.getSession().setAttribute(ORIGINAL_URL, buildOriginalURL(request));
	}

	public static String buildOriginalURL(HttpServletRequest request) {
		StringBuffer originalURL = request.getRequestURL();
		Map parameters = request.getParameterMap();
		if (parameters != null && parameters.size() > 0) {
			originalURL.append("?");
			for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String[] values = (String[]) parameters.get(key);
				for (int i = 0; i < values.length; i++) {
					originalURL.append(key).append("=").append(values[i]).append("&");
				}
			}
		}
		return originalURL.toString();
	}

	private void forbidCache(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragrma", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	private void requireSSOToken(HttpServletResponse response) throws IOException {
		response.setHeader(WWW_AUTHENTICATE_HEAD, NEG_TOKEN);
		if (logger.isDebugEnabled()) {
			logger.debug("Sending response token [" + NEG_TOKEN + "]");
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private String authenticate(byte[] token, HttpServletResponse response) {
		return SSOHandler.authenticate(token, response);
	}

	private boolean isNTLM(byte[] token) {
		if (token.length < 8) {
			return false;
		}
		byte[] buf = new byte[OID_NTLM.length];
		System.arraycopy(token, 0, buf, 0, buf.length);
		return Arrays.equals(buf, OID_NTLM);
	}

	private boolean isMSIE(HttpServletRequest request) {
		String userAgent = request.getHeader(USER_AGENT_HEAD);
		if (StringUtils.isEmpty(userAgent)) {
			logger.warn("userAgent is empty.");
			return false;
		}
		Matcher m = MSIE_PATTERN.matcher(userAgent);
		if (m.find()) {
			m = WINDOW_PATTERN.matcher(userAgent);
			return m.find();
		}
		return false;
	}

	private boolean isLogined(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute(LOING_USER_ID);
		return userId != null;
	}

	private String getMessage(String key) {
		return key;
	}

	private Map buildContextObject(HttpServletRequest request) {
		Map result = new HashMap();
		result.put("application_title", getMessage("application_title"));
		result.put("login_user_id_text", getMessage("login_user_id_text"));
		result.put("login_user_pwd_text", getMessage("login_user_pwd_text"));
		result.put("button_confirm_text", getMessage("button_confirm_text"));
		result.put("button_cancel_text", getMessage("button_cancel_text"));
		result.put("context_path", request.getContextPath());
		result.put("currLocale", getCurrLocale());
		return result;
	}

	public String getCurrLocale() {
		return "zh_CN";
	}

	private String buildSSOLoginPage(HttpServletRequest request) {
		if (template != null) {
			StringWriter writer = new StringWriter();
			try {
				Map root = buildContextObject(request);
				template.process(root, writer);
			} catch (TemplateException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			}
			return writer.toString();
		}
		return "";
	}

}
