/**
 *  Created By: HAAProject
 * Created Date: Jan 14, 2013
 */
package com.haaproject.spring.mvc.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.haaproject.spring.mvc.domain.Foo;
import com.haaproject.spring.mvc.domain.Petclinic;
import com.haaproject.spring.mvc.service.FooService;


/**
 * @author geln
 * @version 1.0
 */
public class TestAopTransaction {
	public static void main(String[] args) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/haaproject/spring/demo/test/applicationContext-aop.xml");
		FooService fooService = (FooService) ctx.getBean("fooService");
		
		fooService.save(new Foo("tom"), new Petclinic("eric"));
		fooService.save(new Foo("tom"), new Petclinic());
		
	}
}
