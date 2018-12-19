package com.tianhe.txmanager.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-10-16 16:15
 */
@Data
public class TransactionGroup implements Serializable{

    private static final long serialVersionUID = -1727663570158847769L;

    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 事务状态
     */
    private String status;

    private List<TransactionItem> transactionItemList = new ArrayList<>();
}
