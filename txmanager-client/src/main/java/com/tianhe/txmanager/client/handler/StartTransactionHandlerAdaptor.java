package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.client.TransactionClientHandlerAdaptor;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-12-04 15:19
 */
@Slf4j
@Service
public class StartTransactionHandlerAdaptor extends TransactionHandler {

    @Autowired
    private TransactionClientHandlerAdaptor transactionClientHandlerAdaptor;

    public boolean saveTransactionGroup(String taskId) {
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(IdUtil.getTransactionGroupId());
        List<TransactionItem> transactionItemList = new ArrayList<>();
        TransactionItem groupItem = super.buildGroupItem(group);
        transactionItemList.add(groupItem);
        TransactionItem item = super.buildTransactionItem(group,taskId);
        transactionItemList.add(item);
        group.setTransactionItemList(transactionItemList);
        boolean saveTransactionGroup = transactionClientHandlerAdaptor.createTransactionGroup(group);
        ManagerContext.INSTANCE.setGroupId(group.getGroupId());
        return saveTransactionGroup;
    }

    public boolean preCommit(String groupId) {
        return transactionClientHandlerAdaptor.preCommit(groupId);
    }

    public void completeCommit(String taskId) {
        transactionClientHandlerAdaptor.commit(ManagerContext.INSTANCE.getGroupId(),taskId, TransactionStatusEnum.COMMIT.getCode());
    }

    public void rollback() {
        transactionClientHandlerAdaptor.rollbackTransactionGroup(ManagerContext.INSTANCE.getGroupId());
    }
}
