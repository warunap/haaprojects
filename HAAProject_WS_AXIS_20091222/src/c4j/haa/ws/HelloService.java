/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Dec 22, 2009 4:14:06 PM $
 *
 * Author: Eric Yang
 * Date  : Dec 22, 2009 4:14:06 PM
 *
 */
package c4j.haa.ws;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class HelloService implements Hello {

	public String getHello(String name) {
		return "hello, " + name;
	}
}