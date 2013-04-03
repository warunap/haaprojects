/**
 * Created Date: 2013-1-24 下午12:53:58
 */
package org.haaproject.kerberos.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		@SuppressWarnings("rawtypes")
		Enumeration attributeNames = req.getSession().getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String name = (String) attributeNames.nextElement();
			req.getSession().removeAttribute(name);
		}
		writer.write("log out");
	}

}
