package com.tianhe.txmanager.client;

import com.tianhe.txmanager.common.ManagerConfig;
import com.tianhe.txmanager.core.SpringHelper;
import com.tianhe.txmanager.core.store.SimpleStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:57
 */
@Component
@Slf4j
public class TransactionController {

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
                ManagerConfig managerConfig = springHelper.getBean(ManagerConfig.class);
                ScheduledExecutorService shutdownScheduledExecutorService = managerConfig.getScheduledExecutorService();
                shutdownScheduledExecutorService.shutdown();
                try {
                    shutdownScheduledExecutorService.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("txManager 关闭调度线程池失败，异常信息={}",e);
                }
                ExecutorService executeService = managerConfig.getExecuteService();
                executeService.shutdown();
                try {
                    executeService.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("txManager 关闭线程池失败，异常信息={}",e);
                }
            }
        }));
    }
}
