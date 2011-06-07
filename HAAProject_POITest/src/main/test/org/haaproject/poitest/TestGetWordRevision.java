/**
 * Created Date: Jun 1, 2011 4:31:23 PM
 */
package org.haaproject.poitest;

import java.io.IOException;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class TestGetWordRevision {

	public static void main(String[] args) {
		String officePath = "g:/a.docx";
		// String officePath = "g:/ss.doc";
		try {
			System.out.println("pagecount:" + OfficeTools.getPageCount(officePath));
			OfficeTools.getWordRevisions(officePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
