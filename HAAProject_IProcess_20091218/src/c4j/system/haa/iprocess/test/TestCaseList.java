/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Jan 6, 2010 1:52:50 PM $
 *
 * Author: Eric Yang
 * Date  : Jan 6, 2010 1:52:50 PM
 *
 */
package c4j.system.haa.iprocess.test;

import junit.framework.TestCase;

import com.comwave.staffware.sso.SSOException;
import com.staffware.sso.data.vACaseContent;
import com.staffware.sso.data.vACaseCriteria;
import com.staffware.sso.data.vException;
import com.staffware.sso.data.vNodeId;
import com.staffware.sso.data.vProcId;
import com.staffware.sso.data.vSortField;
import com.staffware.sso.jbase.sCaseManager;
import com.staffware.sso.jbase.sPageableList;
import com.staffware.sso.jbase.sSession;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class TestCaseList extends TestCase {

	private String hostname = "192.168.5.111";

	private String nodename = "staffw_nod1";

	private int tcp = 1145;

	protected String username = "swadmin";

	private String password = "";

	private sSession session;

	protected void setUp() throws Exception {
		try {
			vNodeId nodeId = new vNodeId(nodename, hostname, hostname, tcp, false);
			session = new sSession(nodeId, username, password);
		} catch (vException e) {
			throw new SSOException("Please specify correct login information!", e);
		}
	}

	public void testGetInfo() throws vException {
		sCaseManager caseManager = session.create_sCaseManager();
		String filterExpr = "SW_QPARAM3=''";
		String procTag = vProcId.makeTag(nodename, "CAR");
		sPageableList caseList = caseManager.getACaseList(new vACaseCriteria(), procTag, new vACaseContent(true), 1);
		//sPageableList caseList = caseManager.getACaseList(procTag);
		System.out.println(caseList.getAvailableCnt());
		System.out.println();
	}
}
