package com.mljr.txmanager.common.utils;

import com.mljr.txmanager.common.enums.IdEnum;

import java.util.UUID;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:25
 */
public abstract class IdUtil {

    /**
     * TODO id先支持单节点，txmanager也先支持单节点
     * @return
     */
    public static String getTransactionId(){
        return IdEnum.TRANSACTION_ITEM.getCode()+"-"+UUID.randomUUID().toString();
    }

    /**
     * 获取事务组的id
     * @return
     */
    public static String getTransactionGroupId(){
        return IdEnum.TRANSACTION_GROUP.getCode()+"-"+UUID.randomUUID().toString();
    }

    public static String getTaskId(){
        return IdEnum.TASK.getCode()+"-"+UUID.randomUUID().toString();
    }
}
