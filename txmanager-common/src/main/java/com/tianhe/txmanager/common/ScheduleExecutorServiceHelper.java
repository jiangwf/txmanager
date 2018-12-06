package com.tianhe.txmanager.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author: he.tian
 * @time: 2018-12-06 17:54
 */
public class ScheduleExecutorServiceHelper {

    public static ScheduledExecutorService INSTANCE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private ScheduleExecutorServiceHelper(){}

}
