package com.tianhe.txmanager;

import com.tianhe.txmanager.common.ExecutorServiceHelper;
import com.tianhe.txmanager.common.ScheduleExecutorServiceHelper;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.SpringHelper;
import com.tianhe.txmanager.core.store.SimpleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:57
 */
@Component
public class TransactionCleaner {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringHelper springHelper;

    public void start(){
        SimpleStore simpleStore = springHelper.getBean(SimpleStore.class);
        ScheduleExecutorServiceHelper.INSTANCE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("=======================================");
                logger.info("txManager 事务组信息===================");
                for (TransactionGroup group:simpleStore.getTransactionGroupMap().values()){
                    logger.info("事务组信息="+group.getGroupId()+","+group.getStatus());
                    for (TransactionItem item : group.getTransactionItemList()) {
                        logger.info("事务信息={}",item);
                    }
                }
                logger.info("=======================================");
            }
        },2,2, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                ScheduleExecutorServiceHelper.INSTANCE.shutdown();
                try {
                    ScheduleExecutorServiceHelper.INSTANCE.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    logger.error("txManager 关闭调度线程池失败，异常信息={}",e);
                }
                ExecutorServiceHelper.INSTANCE.shutdown();
                try {
                    ExecutorServiceHelper.INSTANCE.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    logger.error("txManager 关闭线程池失败，异常信息={}",e);
                }
            }
        }));
    }
}
