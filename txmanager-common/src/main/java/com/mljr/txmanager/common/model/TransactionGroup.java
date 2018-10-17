package com.mljr.txmanager.common.model;

import lombok.Data;

import java.io.Serializable;
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
    private String id;

    /**
     * 事务状态
     */
    private int status;

    private List<TransactionItem> transactionItemList;
}
