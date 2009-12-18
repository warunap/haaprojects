/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Dec 18, 2009 7:40:41 PM $
 *
 * Author: Eric Yang
 * Date  : Dec 18, 2009 7:40:41 PM
 *
 */
package c4j.system.haa.iprocess.test;

import com.comwave.staffware.sso.CaseCore;
import com.comwave.staffware.sso.Field;
import com.comwave.staffware.sso.Step;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class TestCaseCore extends BaseSSOCoreTest {

	CaseCore caseCore;

	protected void setUp() throws Exception {
		super.setUp();
		caseCore = ssoCore.getCaseCore();
	}

	public void testPredictNextStep() {
		Field field1 = new Field("A1", new Integer(20));
		Field field2 = new Field("A2", new Integer(30));
		Field[] fields = new Field[] { field1, field2 };
		Step step = caseCore.predictNextStep("WSTEST", "4122", fields)[0];
		assertEquals("WSTEST", step.getProcName());
		assertEquals("S3", step.getStepName());
	}

}
