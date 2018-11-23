package com.mljr.txmanager.core.service;

import com.mljr.txmanager.common.model.TransactionGroup;
import com.mljr.txmanager.common.model.TransactionItem;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-02 16:53
 */
public interface ManagerService {

    /**
     * 保存事务组，在事务发起方调用
     * @param transactionGroup
     * @return
     */
    void saveTransactionGroup(TransactionGroup transactionGroup);

    /**
     * 往事务组添加事务
     * @param transactionGroupId
     * @param transactionItem
     */
    void addTransaction(String transactionGroupId, TransactionItem transactionItem);

    /**
     * 根据事务组id获取事务组的事务项信息
     * @param transactionGroupId
     * @return
     */
    List<TransactionItem> selectByTransactionGroupId(String transactionGroupId);

    /**
     * 在事务回滚或者事务组中事务项全部提交的时候删除事务组信息
     * @param transactionGroupId
     */
    void removeTransactionGroup(String transactionGroupId);

    /**
     * 更新事务信息
     * @param transactionItem
     */
    void updateTransactionItem(String transactionGroupId,TransactionItem transactionItem);


    /**
     * 获取事务组状态
     * @param transactionGroupId
     * @return
     */
    String selectTransactionGroupStatus(String transactionGroupId);

    /**
     * 更新事务组状态
     * @param transactionGroup
     */
    public void updateTransactionGroupStatus(TransactionGroup transactionGroup);


}
