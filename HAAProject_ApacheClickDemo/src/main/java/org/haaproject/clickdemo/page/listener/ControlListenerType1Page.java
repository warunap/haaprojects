/**
 * Created Date: May 30, 2011 2:39:37 PM
 */
package org.haaproject.clickdemo.page.listener;

import org.apache.click.control.ActionLink;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ControlListenerType1Page extends ListenerLayoutPage {

	/* Set the listener to this object's "onLinkClick" method. */
	private ActionLink myLink = new ActionLink("myLink", this, "onLinkClick");

	private String msg;

	// Constructor ------------------------------------------------------------

	public ControlListenerType1Page() {
		addControl(myLink);
	}

	// Event Handlers ---------------------------------------------------------

	/**
	 * Handle the ActionLink control click event.
	 */
	public boolean onLinkClick() {
		String msg = "ControlListenerPage#" + hashCode() + " object method <tt>onLinkClick()</tt> invoked.";
		addModel("msg", msg);

		return true;
	}
}
