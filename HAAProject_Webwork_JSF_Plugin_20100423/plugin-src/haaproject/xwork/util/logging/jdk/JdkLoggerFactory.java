/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package haaproject.xwork.util.logging.jdk;

import haaproject.xwork.util.logging.Logger;
import haaproject.xwork.util.logging.LoggerFactory;

/**
 * Creates jdk loggers
 */
public class JdkLoggerFactory extends LoggerFactory {

	@Override
	protected Logger getLoggerImpl(Class<?> cls) {
		return new JdkLogger(java.util.logging.Logger.getLogger(cls.getName()));
	}

	@Override
	protected Logger getLoggerImpl(String name) {
		return new JdkLogger(java.util.logging.Logger.getLogger(name));
	}
}
