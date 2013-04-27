/**
 *  Created By: HAAProject
 * Created Date: Jan 14, 2013
 */
package com.haaproject.spring.mvc.service;

import com.haaproject.spring.mvc.domain.Foo;
import com.haaproject.spring.mvc.domain.Petclinic;

/**
 * @author geln
 * @version 1.0
 */
public interface FooService {
	Foo getFoo(String fooName);

	Foo getFoo(String fooName, String barName);

	void save(Foo foo, Petclinic petclinic) throws Exception;

	void updateFoo(Foo foo);

}
