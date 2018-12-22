package com.tianhe.txmanager.core.service;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.store.SimpleStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:28
 */
@Service
public class ManagerHandlerAdaptor implements ManagerHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    public TransactionGroup updateTransactionGroupStatus(TransactionGroup transactionGroup) {
        TransactionGroup group = simpleStore.findTransactionGroup(transactionGroup.getGroupId());
        group.setStatus(transactionGroup.getStatus());
        return transactionGroup;
    }

    @Override
    public void registTransactionItemSize(Long threadNo, Integer transactionItemSize) {
        ManagerContext.INSTANCE.getTransactionItemSizeMap().put(threadNo,transactionItemSize);
        logger.info("txManager 事务单元个数信息========="+ManagerContext.INSTANCE.getTransactionItemSizeMap());
    }

    @Override
    public void ifHasTransactionItemNotDoneBlock(TransactionRequest transactionRequest) {
        Integer transactionItemSize = ManagerContext.INSTANCE.getTransactionItemSizeMap().get(transactionRequest.getThreadNo());
        List<TransactionItem> transactionItemList = simpleStore.findTransactionGroup(transactionRequest.getTransactionGroup().getGroupId()).getTransactionItemList();
        if(transactionItemList.size() != transactionItemSize.intValue()){
            logger.info("txManager 当前事务组还有其他事务单元未执行完毕，事务单元阻塞，等待其他事务单元唤醒，事务组={}，事务单元id={}",
                    transactionRequest.getTransactionGroup().getGroupId(), transactionRequest.getTransactionGroup().getTransactionItemList().get(0).getTaskId());
            return;
        }
    }

    @Override
    public String findTransactionExists(TransactionRequest request) {
        return ManagerContext.INSTANCE.getGroupIdMap().get(request.getThreadNo());
    }
}
