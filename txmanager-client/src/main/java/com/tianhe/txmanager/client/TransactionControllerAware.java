package com.tianhe.txmanager.client;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-12-06 20:31
 */
@Component
public class TransactionControllerAware implements ApplicationContextAware{

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransactionController transactionController = applicationContext.getBean(TransactionController.class);
        transactionController.start();
    }
}
