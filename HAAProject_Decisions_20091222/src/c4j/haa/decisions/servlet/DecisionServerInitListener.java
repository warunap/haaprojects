/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Dec 22, 2009 11:56:34 AM $
 *
 * Author: Eric Yang
 * Date  : Dec 22, 2009 11:56:34 AM
 *
 */
package c4j.haa.decisions.servlet;

import java.io.PrintStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;

import com.corticon.service.ccserver.CcServerFactory;
import com.corticon.service.ccserver.ICcServer;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class DecisionServerInitListener implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(DecisionServerInitListener.class);

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {

		System.out.println("begin init");
		logger.info("begin init");
		ICcServer server = CcServerFactory.getCcServer();
		try {
			logger.info(server.getCcServerInfo());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		PrintStream oldOut = System.out;
		PrintStream oldErr = System.err;

		//redirecting System.out/err to log4j appender
		//System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO), true));
		//System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.ERROR), true));
	}
}