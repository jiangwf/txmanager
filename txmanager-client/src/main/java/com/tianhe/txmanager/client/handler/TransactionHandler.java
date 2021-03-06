package com.tianhe.txmanager.client.handler;

import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.ManagerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author: he.tian
 * @time: 2018-12-06 10:44
 */
public abstract class TransactionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TransactionItem buildGroupItem(TransactionGroup group) {
        TransactionItem item = new TransactionItem();
        item.setStatus(TransactionStatusEnum.BEGIN.getCode());
        item.setCreateDate(new Date());
        try {
            item.setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            logger.error("txManager 构建事务组groupItem，获取本机ip地址失败，异常信息={}", e);
        }
        item.setRole(RoleEnum.GROUP.getCode());
        item.setTaskId(group.getGroupId());
        return item;
    }

    public TransactionItem buildStartTransactionItem(TransactionGroup group,String taskId) {
        TransactionItem item = new TransactionItem();
        item.setTaskId(taskId);
        item.setRole(RoleEnum.START.getCode());
        item.setStatus(TransactionStatusEnum.BEGIN.getCode());
        item.setTransactionGroupId(group.getGroupId());
        try {
            item.setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            logger.error("txManager 构建事务组startItem，获取本机ip地址失败，异常信息={}", e);
        }
        item.setCreateDate(new Date());
        return item;
    }

    public void release(String taskId){
        ManagerContext.INSTANCE.getTaskMap().remove(taskId);
    }

    public TransactionItem buildJoinTransactionItem(Long threadNo,String taskId){
        TransactionItem item = new TransactionItem();
        item.setTaskId(taskId);
        item.setStatus(TransactionStatusEnum.BEGIN.getCode());
        item.setRole(RoleEnum.JOIN.getCode());
        item.setTransactionGroupId(ManagerContext.INSTANCE.getGroupId(threadNo));
        item.setCreateDate(new Date());
        try {
            item.setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            logger.error("txManager 构建事务组joinItem，获取本机ip地址失败，异常信息={}", e);
        }
        return item;
    }

}
