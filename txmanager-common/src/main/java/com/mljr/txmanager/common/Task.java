package com.mljr.txmanager.common;

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

    public Task(){
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void singal(){
        try {
            lock.lock();
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    public void await(){
        try {
            lock.lock();
            condition.await();
        }catch (InterruptedException e){
            log.error("同步处理task失败",e);
        }finally {
            lock.unlock();
        }
    }
}