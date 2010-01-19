/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Dec 18, 2009 7:26:21 PM $
 *
 * Author: Eric Yang
 * Date  : Dec 18, 2009 7:26:21 PM
 *
 */
package c4j.system.haa.iprocess.test;

import com.comwave.staffware.sso.Field;
import com.comwave.staffware.sso.ProcCore;
import com.comwave.staffware.sso.Step;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class TestsSimulateDecision extends BaseSSOCoreTest {

	private ProcCore procCore;

	protected void setUp() throws Exception {
		super.setUp();
		procCore = ssoCore.getProcCore();
	}

	public void testSimulate() {
		Field field1 = new Field("TYPE", "A");
		Field field2 = new Field("ORGID", "01");
		Field field3 = new Field("NEXTADD", "4551");
		Field[] fields = new Field[] { field1, field2, field3 };
		Step nextStep = procCore.simulateNextStep("CREDIT", "SELLAPR", fields)[0];
		assertEquals("MGRAPR", nextStep.getStepName());

		field1 = new Field("TYPE", "B");
		field2 = new Field("ORGID", "01");
		field3 = new Field("NEXTADD", "4551");
		fields = new Field[] { field2, field1, field3 };
		nextStep = procCore.simulateNextStep("CREDIT", "SELLAPR", fields)[0];
		assertEquals("MGRAPR1", nextStep.getStepName());
	}

}
