package com.mljr.txmanager.core;

import com.mljr.txmanager.common.Task;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: he.tian
 * @time: 2018-11-26 15:31
 */
@Component
@Data
public class ManagerContext {

    private ConcurrentHashMap<String,Task> tashMap = new ConcurrentHashMap(512);

    public Task getTask(String taskId){
        Task task = tashMap.get(taskId);
        if(Objects.isNull(task)){
            Task putTask = new Task();
            putTask.setTaskId(taskId);
            tashMap.put(taskId,putTask);
            return putTask;
        }
        return task;
    }
}
