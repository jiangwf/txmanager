package com.tianhe.txmanager.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:53
 */
@Component
public class SpringUtil implements ApplicationContextAware{

    public static final SpringUtil INSTANCE = new SpringUtil();

    private ApplicationContext applicationContext;

    private SpringUtil(){}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> type){
        return applicationContext.getBean(type);
    }
}
