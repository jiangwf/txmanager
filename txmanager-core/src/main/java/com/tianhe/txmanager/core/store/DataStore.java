package com.tianhe.txmanager.core.store;

import com.tianhe.txmanager.common.model.TransactionGroup;

/**
 * @author: he.tian
 * @time: 2018-12-04 11:32
 */
public interface DataStore {

    /**
     * 保存事务组
     * @param transactionGroup
     */
    public void save(TransactionGroup transactionGroup);

    /**
     * 查询事务组
     * @param transactionGroupId
     * @return
     */
    public TransactionGroup findTransactionGroup(String transactionGroupId);

    /**
     * 删除事务组
     * @param transactionGroupId
     */
    public void deleteTransactionGroup(String transactionGroupId);

    /**
     * 更新事务组信息
     * @param transactionGroup
     */
    public void updateTransactionGroup(TransactionGroup transactionGroup);
}
