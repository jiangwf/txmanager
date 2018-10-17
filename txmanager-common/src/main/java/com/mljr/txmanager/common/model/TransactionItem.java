package com.mljr.txmanager.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: he.tian
 * @time: 2018-10-16 16:08
 */
@Data
public class TransactionItem implements Serializable{

    private static final long serialVersionUID = 147437744957842939L;

    private String taskId;

    /**
     * 参与事务者id
     */
    private String transactionId;

    /**
     * 事务状态
     */
    private int status;

    /**
     * 事务角色
     */
    private int role;

    /**
     * 事务组id
     */
    private String transactionGroupId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 执行对象的beanId
     */
    private String beanId;

    /**
     * 执行对象的方法
     */
    private String method;

    /**
     * 操作结果信息
     */
    private Object result;
}
