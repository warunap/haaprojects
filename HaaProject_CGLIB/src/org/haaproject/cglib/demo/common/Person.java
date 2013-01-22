/**
 * 
 * Created Date: Jan 15, 2013
 */
package org.haaproject.cglib.demo.common;

/**
 * @author geln
 * @version 1.0
 */
public class Person {

	// 实现 Person 接口的 sayHello() 方法
	public String sayHello(String name) {
		System.out.println("-- executing sayHello method --");
		// 返回简单的字符串
		return "Hello " + name + "(called from CGLIB)";
	}

	// 定义一个 eat() 方法
	public void eat(String food) {
		System.out.println("I am eating " + food);
	}

}
