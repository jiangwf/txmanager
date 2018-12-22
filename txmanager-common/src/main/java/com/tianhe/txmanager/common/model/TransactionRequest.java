package com.tianhe.txmanager.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: he.tian
 * @time: 2018-10-16 15:27
 */
@Data
public class TransactionRequest implements Serializable{

    private static final long serialVersionUID = 7122401125484059026L;

    /**
     * 执行动作
     */
    private String action;

    /**
     * 执行数据发送任务key
     */
    private String taskId;

    private String result;

    private Long threadNo;

    private Integer transactionItemSize;

    private TransactionGroup transactionGroup;
}
