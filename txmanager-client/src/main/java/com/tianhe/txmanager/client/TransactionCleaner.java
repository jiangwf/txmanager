package com.tianhe.txmanager.client;

import com.tianhe.txmanager.common.ExecutorServiceHelper;
import com.tianhe.txmanager.common.ScheduleExecutorServiceHelper;
import com.tianhe.txmanager.core.SpringHelper;
import com.tianhe.txmanager.core.store.SimpleStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:57
 */
@Component
@Slf4j
public class TransactionCleaner {

    @Autowired
    private SpringHelper springHelper;

    public void start(){
        SimpleStore simpleStore = springHelper.getBean(SimpleStore.class);
        ScheduledExecutorService scheduledExecutorService = springHelper.getBean(ScheduledExecutorService.class);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("txManager 事务组信息={}"+simpleStore.getTransactionGroupMap());
            }
        },1,1, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                ScheduleExecutorServiceHelper.INSTANCE.shutdown();
                try {
                    ScheduleExecutorServiceHelper.INSTANCE.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("txManager 关闭调度线程池失败，异常信息={}",e);
                }
                ExecutorServiceHelper.INSTANCE.shutdown();
                try {
                    ExecutorServiceHelper.INSTANCE.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("txManager 关闭线程池失败，异常信息={}",e);
                }
            }
        }));
    }
}
