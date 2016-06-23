package com.ghw.util;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware,
		Serializable {

	private static ApplicationContext context;

	public ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
}
