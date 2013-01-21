/**
 *  Created By: HAAProject
 * Created Date: Jan 14, 2013
 */
package com.haaproject.spring.demo.domain;

/**
 * @author geln
 * @version 1.0
 */
public class Foo {
	private String id;

	private String name;

	public Foo(String name) {
		setName(name);
	}
	public Foo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
