package com.tianhe.txmanager.core;

import com.tianhe.txmanager.common.Task;
import com.tianhe.txmanager.common.model.TransactionGroup;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: he.tian
 * @time: 2018-11-26 15:31
 */
public class ManagerContext {

    public static ManagerContext INSTANCE = new ManagerContext();

    @Getter
    private ConcurrentMap<String,Task> taskMap = new ConcurrentHashMap(512);

    @Getter
    private ConcurrentMap<Thread,TransactionGroup> transactionGroupMap = new ConcurrentHashMap(512);

    private ManagerContext(){}

    /**
     * 获取task TODO 缓存这块后续优化
     * @param taskId
     * @return
     */
    public Task getTask(String taskId){
        if(Objects.isNull(getTaskMap().get(taskId))){
            Task task = new Task();
            task.setTaskId(taskId);
            getTaskMap().put(taskId,task);
            return task;
        }
        return getTaskMap().get(taskId);
    }
}
