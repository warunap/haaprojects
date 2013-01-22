/**
 * 
 * Created Date: Jan 15, 2013
 */
package org.haaproject.cglib.demo.advice;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author geln
 * @version 1.0
 */
public class AroundAdvice implements MethodInterceptor {

	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("before method executing ...");

		// 執行目標方法，并保存目標方法執行后的返回值
		Object result = proxy.invokeSuper(target, args);

		System.out.println("after method executing ...");

		return "result:" + result;
	}
}
