/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 15, 2010 10:26:56 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 15, 2010 10:26:56 PM
 *
 */
package com.sisopipo.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sisopipo.publisher.PublishThread;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class PublisherStarterListener implements ServletContextListener {
	private static final Log logger = LogFactory.getLog(PublisherStarterListener.class);

	public void contextDestroyed(ServletContextEvent event) {
		logger.info(this.getClass().getName() + " ServletContextListener destroyed!");
	}

	public void contextInitialized(ServletContextEvent event) {
		new PublishThread().start();
		logger.info(this.getClass().getName() + " ServletContextListener Initialized!");
	}

}
