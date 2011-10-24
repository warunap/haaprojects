/**
 * $Revision: 1.1 $
 * $Author: geln_yang $
 * $Date: 2010/06/01 07:33:42 $
 *
 * Author: Eric Yang
 * Date  : Jul 25, 2009 2:08:57 PM
 *
 */
package org.haa.dataconvert.exception;

/**
 * @author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CfgException extends Exception {
	public CfgException() {
		super();
	}

	public CfgException(String msg, Throwable t) {
		super(msg, t);
	}

	public CfgException(String msg) {
		super(msg);
	}

	public CfgException(Throwable t) {
		super(t);
	}
}
