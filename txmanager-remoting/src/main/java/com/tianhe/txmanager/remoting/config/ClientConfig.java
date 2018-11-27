package com.tianhe.txmanager.remoting.config;

import lombok.Data;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:38
 */
@Data
public class ClientConfig {

    /**
     * 延迟时间
     */
    private int delayTime = 30;

    /**
     * 工作线程数
     */
    private int threads = Runtime.getRuntime().availableProcessors();

    /**
     * 心跳检测时间10秒
     */
    private int heartTime = 10;

    /**
     * 执行失败后是否需要补偿
     */
    private boolean compensation = false;

    /**
     * 执行失败最大补偿处理重试次数
     */
    private int retryNum = 5;

    /**
     * txManager事务管理器host
     */
    private String txManagerHost;

    /**
     * txManager事务管理器port
     */
    private Integer txManagerPort;
}
