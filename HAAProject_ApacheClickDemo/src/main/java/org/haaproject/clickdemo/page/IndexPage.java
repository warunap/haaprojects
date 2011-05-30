/**
 * Created Date: May 30, 2011 9:57:50 AM
 */
package org.haaproject.clickdemo.page;

import java.util.Date;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class IndexPage extends BaseLayoutPage {

	private Date time = new Date();

	public IndexPage() {
		setTitle("Index Page");
		addModel("time", time);
	}

}
