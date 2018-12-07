package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.store.SimpleStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:47
 */
@Service
public class JoinTransactionHandlerAdaptor extends TransactionHandler{

    @Autowired
    private SimpleStore simpleStore;

    public JoinTransactionHandlerAdaptor(SimpleStore simpleStore){
        this.simpleStore = simpleStore;
    }

    @Override
    public Object invoke() {
        TransactionGroup group = ManagerContext.INSTANCE.getTransactionGroupMap().get(Thread.currentThread());
        TransactionItem item = buildTransactionItem(group, RoleEnum.JOIN.getCode());
        group.getTransactionItemList().add(item);
        simpleStore.updateTransactionGroup(group);
        return null;
    }
}
