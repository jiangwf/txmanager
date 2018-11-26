package com.mljr.txmanager.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:36
 */
@Configuration
public class ManagerConfig {

    @Bean
    public ScheduledExecutorService getScheduledExecutorService() {
        return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    public ExecutorService getExecuteService(){
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
