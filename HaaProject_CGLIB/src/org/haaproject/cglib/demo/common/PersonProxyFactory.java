/**
 * 
 * Created Date: Jan 15, 2013
 */
package org.haaproject.cglib.demo.common;

import org.haaproject.cglib.demo.advice.AroundAdvice;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author geln
 * @version 1.0
 */
// 該類會通過cglib來為chinese生成代理類
public class PersonProxyFactory {

	public static Person getAuthInstance() {
		Enhancer en = new Enhancer();
		// 設置要代理的目標類
		en.setSuperclass(Person.class);
		// 設置要代理的攔截器
		en.setCallback(new AroundAdvice());
		return (Person) en.create();
	}
}
