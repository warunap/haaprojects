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
public class TestCasePredict extends BaseSSOCoreTest {

	CaseCore caseCore;

	protected void setUp() throws Exception {
		super.setUp();
		caseCore = ssoCore.getCaseCore();
	}

	public void testPredictNextStep() {
		Field field1 = new Field("TYPE", "A");
		Field field2 = new Field("ORGID", "01");
		Field field3 = new Field("NEXTADD", "4551");
		Field[] fields = new Field[] { field1, field2, field3 };
		Step step = caseCore.predictNextStep("CREDIT", "6191", fields)[0];
		assertEquals("MGRAPR", step.getStepName());

		field1 = new Field("TYPE", "B");
		fields = new Field[] { field1, field2, field3 };
		step = caseCore.predictNextStep("CREDIT", "6191", fields)[0];
		assertEquals("MGRAPR1", step.getStepName());

	}
}
