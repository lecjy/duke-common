package com.duke.mvc.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static <T> T getBeanIgnoreNotFound(String name) {
        T t = null;
        try {
            t = getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T getBeanIgnoreNotFound(Class<T> clazz) {
        T t = null;
        try {
            t = getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T getBeanIgnoreNotFound(String name, Class<T> clazz) {
        T t = null;
        try {
            t = getBean(name, clazz);
        } catch (NoSuchBeanDefinitionException e) {
            e.printStackTrace();
        }
        return t;
    }
}
