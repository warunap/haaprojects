/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 16, 2010 1:12:29 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 16, 2010 1:12:29 AM
 *
 */
package com.sisopipo.exception;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class OperatonException extends Exception {

	public OperatonException() {
	}

	public OperatonException(String message) {
		super(message);
	}

	public OperatonException(Throwable cause) {
		super(cause);
	}

	public OperatonException(String message, Throwable cause) {
		super(message, cause);
	}

}
