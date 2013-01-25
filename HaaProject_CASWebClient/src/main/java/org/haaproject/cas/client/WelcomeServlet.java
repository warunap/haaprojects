/**
 * Created Date: 2013-1-24 下午12:53:58
 */
package org.haaproject.cas.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.authentication.AttributePrincipal;

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
		HttpServletRequestWrapper reqWrapper = new HttpServletRequestWrapper(req);
		PrintWriter writer = resp.getWriter();
		String remoteUser = reqWrapper.getRemoteUser();
		writer.write("remoteUser:" + remoteUser + " , ");

		AttributePrincipal principal = (AttributePrincipal) reqWrapper.getUserPrincipal();
		Map<?, ?> attrs = principal.getAttributes();
		if (attrs == null) {
			throw new ServletException("no by the CAS client attributes ");
		}

		// attributes are the same with LDAP
		String puid = (String) attrs.get("puid");
		String email = (String) attrs.get("email");
		String lastname = (String) attrs.get("lastname");
		String firstname = (String) attrs.get("firstname");
		String fullname = (String) attrs.get("fullname");

		writer.write("welcome:" + puid + " , ");
		writer.write("map:" + attrs);
	}
}
