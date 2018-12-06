package com.tianhe.txmanager.core.service;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.store.SimpleStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:28
 */
@Slf4j
@Component
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
    public void addTransaction(String transactionGroupId, TransactionItem transactionItem) {
        TransactionGroup transactionGroup = simpleStore.findTransactionGroup(transactionGroupId);
        transactionGroup.getTransactionItemList().add(transactionItem);
    }

    @Override
    public List<TransactionItem> selectByTransactionGroupId(String transactionGroupId) {
        return simpleStore.findTransactionGroup(transactionGroupId).getTransactionItemList();
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
        return simpleStore.findTransactionGroup(transactionGroupId).getStatus();
    }

    @Override
    public void updateTransactionGroupStatus(TransactionGroup transactionGroup) {
        simpleStore.deleteTransactionGroup(transactionGroup.getGroupId());
        simpleStore.save(transactionGroup);
    }
}
