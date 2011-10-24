/**
 * $Revision: 1.1 $
 * $Author: geln_yang $
 * $Date: 2010/05/20 12:36:00 $
 *
 * Author: Eric Yang
 * Date  : Jul 25, 2009 2:38:03 PM
 *
 */
package org.haa.dataconvert.exception;

/**
 * @author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ParseException extends Exception {

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
