/**
 * Created Date: 2013-1-24 下午12:53:58
 */
package org.haaproject.kerberos.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class WelcomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		String remoteUser = req.getRemoteUser();
		if (remoteUser == null) {
			remoteUser = (String) req.getSession().getAttribute("LOGIN_USER_ID");
		}
		writer.write("remoteUser:" + remoteUser + " , ");

		Principal principal = req.getUserPrincipal();
		if (principal != null) {
			String puid = principal.getName();
			writer.write("welcome:" + puid + " ");
		}
		req.getSession().setAttribute("LOGIN_USER_ID", remoteUser);
	}

}
