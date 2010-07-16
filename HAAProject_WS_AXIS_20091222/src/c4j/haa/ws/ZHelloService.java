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
public class ZHelloService implements ZHello {

	public String 问好(String 姓名) {
		return "你好 " + 姓名;
	}

	public String hello(String name) {
		return "hello " + name;
	}
}