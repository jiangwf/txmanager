package com.tianhe.txmanager.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:38
 */
@Component
@Data
@PropertySource("classpath:client-config.properties")
public class ClientConfig {

    /**
     * 延迟时间
     */
    @Value("${delayTime}")
    private int delayTime = 30;

    /**
     * 工作线程数
     */
    private int threads = Runtime.getRuntime().availableProcessors();

    /**
     * 心跳检测时间10秒
     */
    @Value("${heartTime}")
    private int heartTime = 10;

    /**
     * 执行失败后是否需要补偿
     */
    @Value("${compensation}")
    private boolean compensation = false;

    /**
     * 执行失败最大补偿处理重试次数
     */
    @Value("${retryNum}")
    private int retryNum = 5;

    /**
     * txManager事务管理器host
     */
    @Value("${txManagerHost}")
    private String txManagerHost;

    /**
     * txManager事务管理器port
     */
    @Value("${txManagerPort}")
    private Integer txManagerPort;

    /**
     * 是否使用txManager分布式事务
     */
    @Value("${useTxTransaction}")
    private boolean useTxTransaction;
}
