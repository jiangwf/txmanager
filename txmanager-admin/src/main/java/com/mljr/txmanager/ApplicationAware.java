package com.mljr.txmanager;

import com.mljr.txmanager.common.ManagerConfig;
import com.mljr.txmanager.core.persistence.TransactionMemoryDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:47
 */
@Slf4j
public class ApplicationAware implements ApplicationContextAware{

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransactionMemoryDao transactionMemoryDao = applicationContext.getBean(TransactionMemoryDao.class);
        ScheduledExecutorService scheduledExecutorService = applicationContext.getBean(ScheduledExecutorService.class);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("内存中事务组信息======"+transactionMemoryDao.getTransactionGroupMap());
            }
        },1,1, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                ManagerConfig managerConfig = applicationContext.getBean(ManagerConfig.class);
                ScheduledExecutorService shutdownScheduledExecutorService = managerConfig.getScheduledExecutorService();
                shutdownScheduledExecutorService.shutdown();
                try {
                    shutdownScheduledExecutorService.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("关闭调度线程池失败，异常信息={}",e);
                }
                ExecutorService executeService = managerConfig.getExecuteService();
                executeService.shutdown();
                try {
                    executeService.awaitTermination(5,TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error("关闭线程池失败，异常信息={}",e);
                }
            }
        }));
    }
}
