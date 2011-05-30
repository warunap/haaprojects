/**
 * Created Date: May 30, 2011 2:39:37 PM
 */
package org.haaproject.clickdemo.page.listener;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.ActionLink;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ControlListenerType2Page extends ListenerLayoutPage {

	private ActionLink myLink = new ActionLink("myLink");

	// Constructor ------------------------------------------------------------

	/**
	 * Create a new Page instance.
	 */
	public ControlListenerType2Page() {
		addControl(myLink);

		myLink.setActionListener(new ActionListener() {
			public boolean onAction(Control control) {
				String msg = "ControlListenerPage#" + hashCode() + " object method <tt>onAction()</tt> invoked.";
				addModel("msg", msg);

				return true;
			}
		});
	}
}
