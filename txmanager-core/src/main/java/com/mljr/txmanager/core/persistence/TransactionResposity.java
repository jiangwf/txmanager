package com.mljr.txmanager.core.persistence;

import com.mljr.txmanager.common.model.TransactionGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: he.tian
 * @time: 2018-10-17 11:28
 */
public class TransactionResposity {

    /**
     * 持久化事务组信息
     */
    private Map<String,TransactionGroup> transactionGroupMap = new HashMap<String,TransactionGroup>();

    private Lock lock = new ReentrantLock();

    public void putTransactionGroup(TransactionGroup transactionGroup){
        lock.lock();
        try {
            transactionGroupMap.put(transactionGroup.getId(),transactionGroup);
        }finally {
            lock.unlock();
        }
    }

    public TransactionGroup getTransactionGroup(String id){
        lock.lock();
        try {
            return transactionGroupMap.get(id);
        }finally {
            lock.unlock();
        }
    }
}
