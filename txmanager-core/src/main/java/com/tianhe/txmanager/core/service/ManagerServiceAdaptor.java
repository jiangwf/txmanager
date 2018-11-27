package com.tianhe.txmanager.core.service;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.dao.TransactionMemoryDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:28
 */
@Service
@Slf4j
public class ManagerServiceAdaptor implements ManagerService{

    @Autowired
    private TransactionMemoryDao transactionMemoryDao;

    @Override
    public void saveTransactionGroup(TransactionGroup transactionGroup) {
        transactionMemoryDao.putTransactionGroup(transactionGroup);
    }

    @Override
    public void addTransaction(String transactionGroupId, TransactionItem transactionItem) {
        TransactionGroup transactionGroup = transactionMemoryDao.getTransactionGroup(transactionGroupId);
        transactionGroup.getTransactionItemList().add(transactionItem);
    }

    @Override
    public List<TransactionItem> selectByTransactionGroupId(String transactionGroupId) {
        return transactionMemoryDao.getTransactionGroup(transactionGroupId).getTransactionItemList();
    }

    @Override
    public void removeTransactionGroup(String transactionGroupId) {
        transactionMemoryDao.deleteTransactionGroup(transactionGroupId);
    }

    @Override
    public void updateTransactionItem(String transactionGroupId,TransactionItem updateTransactionItem) {
        TransactionGroup transactionGroup = transactionMemoryDao.getTransactionGroup(transactionGroupId);
        List<TransactionItem> transactionItemList = transactionGroup.getTransactionItemList();
        for(TransactionItem transactionItem : transactionItemList) {
            if(StringUtils.isNotEmpty(updateTransactionItem.getTaskId()) && StringUtils.isNotEmpty(transactionItem.getTaskId()) &&
                    updateTransactionItem.getTaskId().equals(transactionItem.getTaskId())){
                transactionItemList.remove(transactionItem);
                transactionItemList.add(updateTransactionItem);
                break;
            }
        }
    }

    @Override
    public String selectTransactionGroupStatus(String transactionGroupId) {
        return transactionMemoryDao.getTransactionGroup(transactionGroupId).getStatus();
    }

    @Override
    public void updateTransactionGroupStatus(TransactionGroup transactionGroup) {
        transactionMemoryDao.deleteTransactionGroup(transactionGroup.getTransactionId());
        transactionMemoryDao.putTransactionGroup(transactionGroup);
    }
}
