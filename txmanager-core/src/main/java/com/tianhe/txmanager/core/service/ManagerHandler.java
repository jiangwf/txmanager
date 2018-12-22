package com.tianhe.txmanager.core.service;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-02 16:53
 */
public interface ManagerHandler {

    /**
     * 保存事务组，在事务发起方调用
     * @param transactionGroup
     * @return
     */
    public void saveTransactionGroup(TransactionGroup transactionGroup);

    /**
     * 往事务组添加事务
     * @param transactionGroupId
     * @param transactionItem
     */
    public void addTransaction(TransactionGroup transactionGroup, TransactionItem transactionItem);

    /**
     * 根据事务组id获取事务组的事务项信息
     * @param transactionGroupId
     * @return
     */
    public TransactionGroup selectByTransactionGroupId(String transactionGroupId);

    /**
     * 在事务回滚或者事务组中事务项全部提交的时候删除事务组信息
     * @param transactionGroupId
     */
    public void removeTransactionGroup(String transactionGroupId);

    /**
     * 更新事务信息
     * @param transactionItem
     */
    public void updateTransactionItem(String transactionGroupId,TransactionItem transactionItem);


    /**
     * 获取事务组状态
     * @param transactionGroupId
     * @return
     */
    public String selectTransactionGroupStatus(String transactionGroupId);

    /**
     * 更新事务组状态
     * @param transactionGroup
     */
    public void updateTransactionGroupStatus(TransactionGroup transactionGroup);


}
