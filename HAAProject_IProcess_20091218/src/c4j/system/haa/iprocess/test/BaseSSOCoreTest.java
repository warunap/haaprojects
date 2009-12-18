package c4j.system.haa.iprocess.test;

import java.text.SimpleDateFormat;

import com.comwave.staffware.sso.SSOCore;

import junit.framework.TestCase;

/**
 * @author bribin
 */
public abstract class BaseSSOCoreTest extends TestCase {

	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	private String hostname = "192.168.5.111";

	private String nodename = "staffw_nod1";

	private int tcp = 1145;

	protected String username = "swadmin";

	private String password = "";

	protected SSOCore ssoCore;

	protected void setUp() throws Exception {
		ssoCore = new SSOCore();
		ssoCore.login(hostname, nodename, tcp, false, username, password);
	}

	protected void tearDown() throws Exception {
		ssoCore.logout();
	}

}
