/**
 * 
 * Created Date: Jan 15, 2013
 */
package org.haaproject.cglib.demo.test;

import org.haaproject.cglib.demo.common.Person;
import org.haaproject.cglib.demo.common.PersonProxyFactory;

/**
 * @author geln
 * @version 1.0
 */
public class TestMain {

	public static void main(String[] args) {
		Person person = PersonProxyFactory.getAuthInstance();
		System.out.println(person.getClass());

		System.out.println(person.sayHello("Geln"));
		person.eat("rice");

	}
}
