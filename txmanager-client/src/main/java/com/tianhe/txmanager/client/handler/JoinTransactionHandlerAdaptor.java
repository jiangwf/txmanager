package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.store.SimpleStore;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:47
 */
public class JoinTransactionHandlerAdaptor extends TransactionHandler{

    private SimpleStore simpleStore;

    private ManagerContext managerContext;

    public JoinTransactionHandlerAdaptor(SimpleStore simpleStore,ManagerContext managerContext){
        this.simpleStore = simpleStore;
        this.managerContext = managerContext;
    }

    @Override
    public Object invoke() {
        TransactionGroup group = managerContext.getTransactionGroupMap().get(Thread.currentThread());
        TransactionItem item = buildTransactionItem(group, RoleEnum.JOIN.getCode());
        group.getTransactionItemList().add(item);
        simpleStore.updateTransactionGroup(group);
        return null;
    }
}
