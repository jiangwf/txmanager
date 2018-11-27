package com.tianhe.txmanager.client;

import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:45
 */
public interface TransactionService {

    /**
     * 创建事务组
     * @param transactionGroup
     * @return
     */
    boolean createTransactionGroup(TransactionGroup transactionGroup);

    /**
     * 添加事务
     * @param transactionGroupId
     * @param transactionItem
     * @return
     */
    boolean addTransaction(String transactionGroupId, TransactionItem transactionItem);

    /**
     * 查找事务组状态
     * @param transactionGroupId
     * @return
     */
    String findTransactionGroupStatus(String transactionGroupId);

    /**
     * 查找事务组信息
     * @param transactionGroupId
     * @return
     */
    TransactionGroup findTransactionGroup(String transactionGroupId);

    /**
     * 回滚事务组
     * @param transactionGroupId
     */
    void rollbackTransactionGroup(String transactionGroupId);

    /**
     * 预提交
     * @param transactionGroupId
     * @return
     */
    boolean preCommit(String transactionGroupId);

    /**
     * 提交
     * @param transactionGroupId
     * @param taskId
     * @param status
     * @return
     */
    boolean commit(String transactionGroupId,String taskId,String status);


}
