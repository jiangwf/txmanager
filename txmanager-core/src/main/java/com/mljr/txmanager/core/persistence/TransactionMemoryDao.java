package com.mljr.txmanager.core.persistence;

import com.mljr.txmanager.common.model.TransactionGroup;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: he.tian
 * @time: 2018-10-17 11:28
 */
@Repository
public class TransactionMemoryDao {

    /**
     * 持久化事务组信息
     */
    @Getter
    private Map<String,TransactionGroup> transactionGroupMap = new HashMap<String,TransactionGroup>();

    private Lock lock = new ReentrantLock();

    /**
     * 保存事务组
     * @param transactionGroup
     */
    public void putTransactionGroup(TransactionGroup transactionGroup){
        lock.lock();
        try {
            transactionGroupMap.put(transactionGroup.getId(),transactionGroup);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 获取事务组
     * @param transactionGroupId
     * @return
     */
    public TransactionGroup getTransactionGroup(String transactionGroupId){
        lock.lock();
        try {
            return transactionGroupMap.get(transactionGroupId);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 删除事务组
     * @param transactionGroupId
     */
    public void deleteTransactionGroup(String transactionGroupId){
        lock.lock();
        try {
            transactionGroupMap.remove(transactionGroupId);
        }finally {
            lock.unlock();
        }
    }
}
