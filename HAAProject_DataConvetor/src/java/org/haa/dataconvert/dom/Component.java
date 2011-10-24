/**
 * $Revision: 1.9 $
 * $Author: geln_yang $
 * $Date: 2011/09/06 12:51:17 $
 *
 * Author: Eric Yang
 * Date  : May 19, 2010 5:17:32 PM
 *
 */
package org.haa.dataconvert.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Component extends Container {

	protected List<Container> children;

	/** whether process batched */
	private boolean batched = true;

	/** whether the data content has line flag */
	private boolean hasLineFlag = true;

	/** used to cut line from data just when {@link #hasLineFlag}==false */
	private int lineSize = 0;

	/* ========================================== */

	public boolean isBelongToMe(String firstLineContent) {
		for (int i = 0; i < children.size(); i++) {
			Container c = children.get(i);
			boolean belongToMe = c.isBelongToMe(firstLineContent);
			if (c.isShowOnce() || c.isShowMany())
				return belongToMe;
			else if (belongToMe)
				return true;
		}
		return true;
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

	public boolean isBatched() {
		return batched;
	}

	public void setBatched(boolean batched) {
		this.batched = batched;
	}

	public boolean isHasLineFlag() {
		return hasLineFlag;
	}

	public void setHasLineFlag(boolean hasLineFlag) {
		this.hasLineFlag = hasLineFlag;
	}

	public int getLineSize() {
		return lineSize;
	}

	public void setLineSize(int lineSize) {
		this.lineSize = lineSize;
	}

}
