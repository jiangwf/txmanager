package com.tianhe.txmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-12-06 20:31
 */
@Component
public class TransactionCleanerAware implements ApplicationContextAware{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransactionCleaner transactionCleaner = applicationContext.getBean(TransactionCleaner.class);
        transactionCleaner.start();
        logger.info("txManager 启动transactionCleaner");
    }
}
