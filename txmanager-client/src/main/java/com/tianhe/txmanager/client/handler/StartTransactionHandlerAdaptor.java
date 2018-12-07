package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.store.SimpleStore;
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
    private SimpleStore simpleStore;

    public StartTransactionHandlerAdaptor(SimpleStore simpleStore){
        this.simpleStore = simpleStore;
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
        ManagerContext.INSTANCE.getTransactionGroupMap().put(Thread.currentThread(),group);
        return item;
    }
}
