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

    public boolean saveTransactionGroup(String taskId,String transactionGroupId) {
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(transactionGroupId);
        List<TransactionItem> transactionItemList = new ArrayList<>();
        TransactionItem groupItem = super.buildGroupItem(group);
        transactionItemList.add(groupItem);
        TransactionItem item = super.buildStartTransactionItem(group,taskId);
        transactionItemList.add(item);
        group.setTransactionItemList(transactionItemList);
        boolean saveTransactionGroup = transactionClientHandlerAdaptor.createTransactionGroup(group);
        ManagerContext.INSTANCE.setGroupId(group.getGroupId());
        return saveTransactionGroup;
    }

    public boolean preCommit(String groupId) {
        return transactionClientHandlerAdaptor.preCommit(groupId);
    }

    public boolean completeCommit(String taskId) {
        return transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(), taskId, TransactionStatusEnum.COMMIT.getCode());
    }

    public void rollbackTransactionGroup() {
        transactionClientHandlerAdaptor.rollbackTransactionGroup(ManagerContext.INSTANCE.getGroupId());
    }

    public boolean addTransaction(String taskId) {
        return transactionClientHandlerAdaptor.addTransaction(ManagerContext.INSTANCE.getGroupId(), super.buildJoinTransactionItem(taskId));
    }

    public String findTransactionGroupStatus(String groupId) {
        return transactionClientHandlerAdaptor.findTransactionGroupStatus(groupId);
    }

    public boolean rollbackTransactionItem(String taskId) {
        return transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(), taskId, TransactionStatusEnum.ROLLBACK.getCode());
    }

    public boolean fail(String taskId) {
       return transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(), taskId, TransactionStatusEnum.FAIL.getCode());
    }
}
