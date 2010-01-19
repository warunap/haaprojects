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

import com.comwave.staffware.sso.CaseCore;
import com.comwave.staffware.sso.Field;
import com.comwave.staffware.sso.SSOException;
import com.comwave.staffware.sso.WorkItem;
import com.staffware.sso.data.vException;
import com.staffware.sso.data.vNodeId;
import com.staffware.sso.data.vWorkItem;
import com.staffware.sso.data.vWorkQId;
import com.staffware.sso.jbase.sPageableListR;
import com.staffware.sso.jbase.sSession;
import com.staffware.sso.jbase.sWorkQ;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class TestGetProcessInfo extends TestCase {

	private String hostname = "192.168.5.111";

	private String nodename = "staffw_nod1";

	private int tcp = 1145;

	protected String username = "001";

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
		boolean isReleased = true;
		String workQName = "001";
		String tag = vWorkQId.makeTag(nodename, workQName, isReleased);
		sWorkQ workQ = session.create_sWorkQ(tag);
		sPageableListR workItemList = workQ.getWorkItemList();
		int availableCnt = workItemList.getAvailableCnt();
		for (int i = 0; i < availableCnt; i++) {
			vWorkItem workItem = (vWorkItem) workItemList.getItem(i);
			String workItemTag = workItem.getTag();
			String stepName = workItem.getStepName();
			String caseNumber = workItem.getCaseNumber();
			String description = workItem.getDescription();
			String workQParam1 = workItem.getWorkQParam1();
			String workQParam2 = workItem.getWorkQParam2();
			String workQParam3 = workItem.getWorkQParam3();
			String workQParam4 = workItem.getWorkQParam4();
			System.out.println();
		}

		System.out.println();
	}
}
