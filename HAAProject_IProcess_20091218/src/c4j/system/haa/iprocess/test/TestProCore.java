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
public class TestProCore extends BaseSSOCoreTest {

	private ProcCore procCore;

	protected void setUp() throws Exception {
		super.setUp();
		procCore = ssoCore.getProcCore();
	}

	public void testSimulate() {
		Field field1 = new Field("A1", new Integer(20));
		Field field2 = new Field("A2", new Integer(30));
		Field[] fields = new Field[] { field1, field2 };
		Step nextStep = procCore.simulateNextStep("WSTEST", "S1", fields)[0];
		assertEquals("S3", nextStep.getStepName());

		field1 = new Field("A1", new Integer(40));
		field2 = new Field("A2", new Integer(30));
		fields = new Field[] { field2, field1 };
		nextStep = procCore.simulateNextStep("WSTEST", "S1", fields)[0];
		assertEquals("S2", nextStep.getStepName());
	}

}
