package com.tianhe.txmanager.core.service;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.store.SimpleStore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:28
 */
@Service
public class ManagerHandlerAdaptor implements ManagerHandler {

    @Autowired
    private SimpleStore simpleStore;

    public ManagerHandlerAdaptor(SimpleStore simpleStore){
        this.simpleStore = simpleStore;
    }

    @Override
    public void saveTransactionGroup(TransactionGroup transactionGroup) {
        simpleStore.save(transactionGroup);
    }

    @Override
    public void addTransaction(TransactionGroup transactionGroup, TransactionItem transactionItem) {
        TransactionGroup group = simpleStore.findTransactionGroup(transactionGroup.getGroupId());
        group.getTransactionItemList().add(transactionItem);
        group.setStatus(transactionGroup.getStatus());
    }

    @Override
    public TransactionGroup selectByTransactionGroupId(String transactionGroupId) {
        return simpleStore.findTransactionGroup(transactionGroupId);
    }

    @Override
    public void removeTransactionGroup(String transactionGroupId) {
        simpleStore.deleteTransactionGroup(transactionGroupId);
    }

    @Override
    public void updateTransactionItem(String transactionGroupId,TransactionItem updateTransactionItem) {
        TransactionGroup transactionGroup = simpleStore.findTransactionGroup(transactionGroupId);
        List<TransactionItem> transactionItemList = transactionGroup.getTransactionItemList();
        for(TransactionItem transactionItem : transactionItemList) {
            if(StringUtils.isNotEmpty(updateTransactionItem.getTaskId()) &&
                    updateTransactionItem.getTaskId().equals(transactionItem.getTaskId())){
                transactionItem.setStatus(updateTransactionItem.getStatus());
                break;
            }
        }
    }

    @Override
    public String selectTransactionGroupStatus(String transactionGroupId) {
        return simpleStore.findTransactionGroup(transactionGroupId).getStatus();
    }

    @Override
    public void updateTransactionGroupStatus(TransactionGroup transactionGroup) {
        simpleStore.deleteTransactionGroup(transactionGroup.getGroupId());
        simpleStore.save(transactionGroup);
    }
}
