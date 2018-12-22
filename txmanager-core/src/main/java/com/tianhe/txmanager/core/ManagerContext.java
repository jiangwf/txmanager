package com.tianhe.txmanager.core;

import com.tianhe.txmanager.common.Task;
import com.tianhe.txmanager.common.exception.TransactionException;
import com.tianhe.txmanager.common.utils.StringUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: he.tian
 * @time: 2018-11-26 15:31
 */
public class ManagerContext {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static ManagerContext INSTANCE = new ManagerContext();

    @Getter
    private ConcurrentMap<String,Task> taskMap = new ConcurrentHashMap(512);

    @Getter
    private ConcurrentMap<Long,String> groupIdMap = new ConcurrentHashMap<>();

    @Getter
    private ConcurrentMap<Long,Integer> transactionItemSizeMap = new ConcurrentHashMap<>();

    private ManagerContext(){}

    /**
     * 获取task TODO 缓存这块后续优化
     * @param taskId
     * @return
     */
    public Task getTask(String taskId){
        logger.info("txManager 查询任务taskId={}",taskId);
        if(Objects.isNull(getTaskMap().get(taskId))){
        logger.info("txManager 查询任务taskId={}不存在,重新创建",taskId);
            Task task = new Task();
            task.setTaskId(taskId);
            getTaskMap().put(taskId,task);
            return task;
        }
        return getTaskMap().get(taskId);
    }

   public String getGroupId(Long threadNo){
        return groupIdMap.get(threadNo);
   }

   public void setGroupId(Long threadNo, String groupId){
       if(StringUtil.isNotEmpty(groupIdMap.get(threadNo))){
           throw new TransactionException("txManager 设置事务组id="+groupId+"，当前线程已存在");
       }
       groupIdMap.put(threadNo,groupId);
   }

   public void removeGroupId(Long threadNo){
       groupIdMap.remove(threadNo);
   }
}
