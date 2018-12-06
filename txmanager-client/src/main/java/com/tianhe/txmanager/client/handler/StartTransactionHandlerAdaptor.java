package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.store.SimpleStore;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-12-04 15:19
 */
@Slf4j
public class StartTransactionHandlerAdaptor extends TransactionHandler {

    private SimpleStore simpleStore;

    private ManagerContext managerContext;

    public StartTransactionHandlerAdaptor(SimpleStore simpleStore,ManagerContext managerContext){
        this.simpleStore = simpleStore;
        this.managerContext = managerContext;
    }

    @Override
    public Object invoke() {
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(IdUtil.getTransactionId());
        List<TransactionItem> transactionItemList = new ArrayList<>();
        TransactionItem item = super.buildTransactionItem(group,RoleEnum.START.getCode());
        transactionItemList.add(item);
        group.setTransactionItemList(transactionItemList);
        simpleStore.save(group);
        managerContext.getTransactionGroupMap().put(Thread.currentThread(),group);
        return item;
    }
}
