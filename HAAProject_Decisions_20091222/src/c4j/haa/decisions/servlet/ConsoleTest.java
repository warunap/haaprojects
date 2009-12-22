/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Dec 22, 2009 2:30:09 PM $
 *
 * Author: Eric Yang
 * Date  : Dec 22, 2009 2:30:09 PM
 *
 */
package c4j.haa.decisions.servlet;

import java.util.Collection;

import com.corticon.service.ccserver.CcServerFactory;
import com.corticon.service.ccserver.ICcServer;
import com.corticon.service.ccserver.exception.CcServerInvalidArgumentException;
import com.corticon.service.ccserver.exception.CcServerInvalidCddException;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class ConsoleTest {

	public static void main(String[] args) throws CcServerInvalidArgumentException, CcServerInvalidCddException {
		ICcServer server = CcServerFactory.getCcServer();
		server.loadFromCddDir("i:/cdd/");
		Collection decisionServiceNames = server.getDecisionServiceNames();
		System.out.println(decisionServiceNames);
		String ccServerInfo = server.getCcServerInfo();
		System.out.println(ccServerInfo);
	}
}
