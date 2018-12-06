package com.tianhe.txmanager.core.store;

import com.tianhe.txmanager.common.model.TransactionGroup;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** TODO 由客户端注入spring容器
 * @author: he.tian
 * @time: 2018-10-17 11:28
 */
@Component
public class SimpleStore implements DataStore{

    @Getter
    private Map<String,TransactionGroup> transactionGroupMap = new HashMap<String,TransactionGroup>();

    private Lock lock = new ReentrantLock();

    @Override
    public void save(TransactionGroup transactionGroup) {
        lock.lock();
        try {
            transactionGroupMap.put(transactionGroup.getGroupId(),transactionGroup);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public TransactionGroup findTransactionGroup(String transactionGroupId) {
        lock.lock();
        try {
            return transactionGroupMap.get(transactionGroupId);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteTransactionGroup(String transactionGroupId){
        lock.lock();
        try {
            transactionGroupMap.remove(transactionGroupId);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void updateTransactionGroup(TransactionGroup transactionGroup) {
        lock.lock();
        try {
            transactionGroupMap.remove(transactionGroup.getGroupId());
            transactionGroupMap.put(transactionGroup.getGroupId(),transactionGroup);
        }finally {
            lock.unlock();
        }
    }
}
