/**
 *  Created By: HAAProject
 * Created Date: Jan 14, 2013
 */
package com.haaproject.spring.mvc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.haaproject.spring.mvc.domain.Foo;
import com.haaproject.spring.mvc.domain.Petclinic;

;

/**
 * @author geln
 * @version 1.0
 */
public class DefaultFooService implements FooService {
	
	private static final Log logger = LogFactory.getLog(DefaultFooService.class);

	private SessionFactory sessionFactory;
	
	public com.haaproject.spring.mvc.domain.Foo getFoo(String fooName) {
		throw new UnsupportedOperationException();
	}

	public Foo getFoo(String fooName, String barName) {
		throw new UnsupportedOperationException();
	}

	public void save(Foo foo, Petclinic petclinic) throws Exception {
		logger.info("start save");
		sessionFactory.getCurrentSession().save(foo);
		sessionFactory.getCurrentSession().save(petclinic);
		logger.info("end save");
	}

	public void updateFoo(Foo foo) {
		throw new UnsupportedOperationException();
	}

	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	

}
