package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.client.TransactionClientHandlerAdaptor;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.ManagerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-12-04 15:19
 */
@Service
public class TransactionHandlerAdaptor extends TransactionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TransactionClientHandlerAdaptor transactionClientHandlerAdaptor;

    public boolean saveTransactionGroup(Long threadNo, String taskId,String transactionGroupId) {
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(transactionGroupId);
        List<TransactionItem> transactionItemList = new ArrayList<>();
        TransactionItem groupItem = super.buildGroupItem(group);
        transactionItemList.add(groupItem);
        TransactionItem item = super.buildStartTransactionItem(group,taskId);
        transactionItemList.add(item);
        group.setTransactionItemList(transactionItemList);
        boolean saveTransactionGroup = transactionClientHandlerAdaptor.createTransactionGroup(group);
        ManagerContext.INSTANCE.setGroupId(threadNo,group.getGroupId());
        return saveTransactionGroup;
    }

    public boolean preCommit(String groupId) {
        return transactionClientHandlerAdaptor.preCommit(groupId);
    }

    public boolean completeCommit(Long threadNo, String taskId) {
        return transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(threadNo), taskId, TransactionStatusEnum.COMMIT.getCode());
    }

    public void rollbackTransactionGroup(Long threadNo) {
        transactionClientHandlerAdaptor.rollbackTransactionGroup(ManagerContext.INSTANCE.getGroupId(threadNo));
    }

    public boolean addTransaction(Long threadNo,String taskId) {
        return transactionClientHandlerAdaptor.addTransaction(ManagerContext.INSTANCE.getGroupId(threadNo), super.buildJoinTransactionItem(threadNo,taskId));
    }

    public String findTransactionGroupStatus(String groupId) {
        return transactionClientHandlerAdaptor.findTransactionGroupStatus(groupId);
    }

    public boolean rollbackTransactionItem(Long threaNo,String taskId) {
        return transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(threaNo), taskId, TransactionStatusEnum.ROLLBACK.getCode());
    }

    public boolean fail(Long threadNo,String taskId) {
       return transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(threadNo), taskId, TransactionStatusEnum.FAIL.getCode());
    }
}
