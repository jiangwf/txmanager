package com.tianhe.txmanager.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-12-06 20:31
 */
@Component
@Slf4j
public class TransactionCleanerAware implements ApplicationContextAware{

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransactionCleaner transactionCleaner = applicationContext.getBean(TransactionCleaner.class);
        transactionCleaner.start();
        log.info("txManager 启动transactionCleaner");
    }
}
