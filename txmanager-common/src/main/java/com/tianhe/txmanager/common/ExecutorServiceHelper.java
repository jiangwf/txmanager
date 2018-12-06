package com.tianhe.txmanager.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: he.tian
 * @time: 2018-12-06 17:57
 */
public class ExecutorServiceHelper {

    public static ExecutorService INSTANCE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ExecutorServiceHelper(){}
}
