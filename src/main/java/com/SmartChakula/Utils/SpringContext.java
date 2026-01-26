package com.SmartChakula.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Component
@Log
public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext context;

	public static <T extends Object> T getBean(Class<T> beanClass) {
		if (context != null)
			return context.getBean(beanClass);
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContext.context = context;
//		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}

	public void log(String message) {
		System.out.println(message);
	}
}
