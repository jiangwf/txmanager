package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:44
 */
@Slf4j
public abstract class TransactionHandler {

    public abstract Object invoke();

    public TransactionItem buildTransactionItem(TransactionGroup group, String role) {
        TransactionItem item = new TransactionItem();
        item.setCreateDate(new Date());
        try {
            item.setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            log.error("获取本机ip地址失败，异常信息={}", e);
        }
        item.setRole(role);
        item.setTaskId(IdUtil.getTaskId());
        item.setTransactionGroupId(group.getGroupId());
        return item;
    }
}
