package com.mljr.txmanager.core.model;

import com.mljr.txmanager.common.model.TransactionGroup;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: he.tian
 * @time: 2018-11-02 11:09
 */
@Data
public class NettyRequest implements Serializable {

    private static final long serialVersionUID = -6595403308483086825L;

    /**
     * netty client请求类型
     */
    private int action;

    /**
     * 请求任务类型的key
     */
    private String key;

    /**
     * 请求结果
     */
    private int result;

    private TransactionGroup transactionGroup;
}
