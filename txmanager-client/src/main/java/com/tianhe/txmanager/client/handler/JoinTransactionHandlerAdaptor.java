package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.store.SimpleStore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:47
 */
public class JoinTransactionHandlerAdaptor extends TransactionHandler{

    @Autowired
    private SimpleStore simpleStore;

    @Autowired
    private ManagerContext managerContext;

    @Override
    public Object invoke() {
        TransactionGroup group = managerContext.getTransactionGroupMap().get(Thread.currentThread());
        TransactionItem item = buildTransactionItem(group, RoleEnum.JOIN.getCode());
        group.getTransactionItemList().add(item);
        simpleStore.updateTransactionGroup(group);
        return null;
    }
}
