/**
 *      Project:WebDownloader       
 *    File Name:Downloader.java	
 * Created Time:2012-10-27下午9:13:24
 */
package org.haaproject.security.demo;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.haaproject.security.SecurityTool;
import org.haaproject.security.web.AuthCenterDemoDB;

/**
 * @author Geln Yang
 *
 */
public class AppLoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	class SaltObj{
		String salt;
		Date createTime;
		public SaltObj(String s) {
			salt=s;
			createTime =new Date();
		}
	}

	private static final String DIGEST_ALGORITHM = "MD5";
	private static final String APP_NAME = "demoapp";
	
	private static Map<String,SaltObj> saltMap = new HashMap<String, AppLoginServlet.SaltObj>();
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getSession().getId();
		String type = req.getParameter("t");
		if("salt".equalsIgnoreCase(type)) {
			String salt = RandomStringUtils.randomAlphanumeric(10);
			saltMap.put(id, new SaltObj(salt) );
			resp.getWriter().write(salt);
			
		}else {
			String authKey = req.getParameter("authkey");
			if(StringUtils.isBlank(authKey)) {
				resp.getWriter().write("no authkey");
				return ;
			}
			
			try {
				if(validLoginByGameServer(id, authKey)) {
					resp.getWriter().write("login success");
				}else {
					resp.getWriter().write("login failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
				resp.getWriter().write("has sys error");
			}
		}
	}
	
	private static boolean validLoginByGameServer(String sessionid, String authKey) throws Exception {
		SaltObj saltObj = saltMap.get(sessionid);
		String dbsalt = saltObj.salt;
		String password = AuthCenterDemoDB.getDESByAppName(APP_NAME);

		String digest = SecurityTool.digest(DIGEST_ALGORITHM, password);
		String digestFirst = digest.substring(0, digest.length() / 2);
		String digestSecond = digest.substring(digestFirst.length());

		String symmetricDecrypt = SecurityTool.symmetricDecrypt(digestSecond, authKey);
		String encyptSalt = StringUtils.substringBefore(symmetricDecrypt, ",");

		String signature = StringUtils.substringAfter(symmetricDecrypt, ",");

		String signatureValid = SecurityTool.digest(DIGEST_ALGORITHM, encyptSalt + digestSecond);
		if (!signature.equals(signatureValid)) {
			throw new Exception("signature not valid!");
		}

		String newsalt = SecurityTool.symmetricDecrypt(digestFirst, encyptSalt);
		String salt = StringUtils.substringBefore(newsalt, ",");
		String username = StringUtils.substringAfter(newsalt, ",");
		if (dbsalt.equals(salt)) {
			System.out.println("add to server login record:" + username);
			return true;
		}
		return false;
	}
}
