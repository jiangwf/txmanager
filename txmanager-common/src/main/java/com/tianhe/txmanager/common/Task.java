package com.tianhe.txmanager.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: he.tian
 * @time: 2018-11-22 15:53
 */
@Slf4j
@Data
public class Task {

    private String taskId;

    private Lock lock;

    private Condition condition;

    private Object result;

    private boolean notify;

    public Task(){
        lock = new ReentrantLock();
        condition = lock.newCondition();
        setNotify(false);
    }

    public void singal(){
        try {
            lock.lock();
            condition.signal();
            log.info("txManager 事务组事务taskId={}释放锁",taskId);
            setNotify(true);
        }finally {
            lock.unlock();
        }
    }

    public void await(){
        try {
            lock.lock();
            condition.await();
            log.info("txManager 事务组事务taskId={}获取锁",taskId);
        }catch (InterruptedException e){
            log.error("txManager 同步处理task失败",e);
        }finally {
            lock.unlock();
        }
    }
}
