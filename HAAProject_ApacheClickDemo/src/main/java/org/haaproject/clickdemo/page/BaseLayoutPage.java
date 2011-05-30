/**
 * Created Date: May 30, 2011 10:26:05 AM
 */
package org.haaproject.clickdemo.page;

import org.apache.click.Page;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class BaseLayoutPage extends Page {

	public String title = "Home";

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getTemplate() {
		return "/listener/border-template.htm";
	}
}
