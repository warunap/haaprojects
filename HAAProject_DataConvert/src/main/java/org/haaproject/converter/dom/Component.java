/**
 * $Revision: 1.9 $ $Author: geln_yang $ $Date: 2011/09/06 12:51:17 $
 * 
 * Author: Eric Yang Date : May 19, 2010 5:17:32 PM
 * 
 */
package org.haaproject.converter.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Component extends Container {

	protected List<Container> children;

	/* ========================================== */

	public boolean isBelongToMe(String lineContent) {
		for (int i = 0; i < children.size(); i++) {
			Container c = children.get(i);
			if (c.isBelongToMe(lineContent))
				return true;
		}
		return false;
	}

	public void addComponent(Container child) {
		if (children == null)
			children = new ArrayList<Container>();
		children.add(child);
		child.setParent(this);
	}

	public void setBatchReadFlag() {
		setReadFlag();
		if (children != null) {
			for (Container c : children) {
				c.setReadFlag();
			}
		}
	}

	public void resetBatchReadFlag() {
		resetReadFlag();
		if (children != null) {
			for (Container c : children) {
				c.resetReadFlag();
			}
		}
	}

	/* ========================================== */
	public List<Container> getChildren() {
		return children;
	}

	public void setChildren(List<Container> children) {
		this.children = children;
	}

}
