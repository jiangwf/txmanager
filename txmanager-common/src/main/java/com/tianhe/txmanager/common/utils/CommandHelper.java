package com.tianhe.txmanager.common.utils;

import com.tianhe.txmanager.common.NettyManager;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import io.netty.channel.Channel;

import java.util.Collections;

/**
 * @author: he.tian
 * @time: 2018-11-23 17:45
 */
public abstract class CommandHelper {

    /**
     * 构建客户端响应报文
     * @param transactionItem
     * @param transactionStatusEnum
     * @return
     */
    public static TransactionRequest buildCommand(TransactionItem transactionItem, TransactionStatusEnum transactionStatusEnum, Channel channel){
        TransactionRequest request = new TransactionRequest();
        TransactionGroup group = new TransactionGroup();
        channel = NettyManager.getInstance().getChannelByRemoteAddr(transactionItem.getRemoteAddr());
        if(TransactionStatusEnum.ROLLBACK.getCode().equals(transactionStatusEnum.getCode())){
            request.setAction(ActionEnum.ROLLBACK.getCode());
            transactionItem.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
            group.setStatus(ActionEnum.ROLLBACK.getCode());
        }else if(TransactionStatusEnum.COMMIT.getCode().equals(transactionStatusEnum.getCode())){
            request.setAction(ActionEnum.COMMIT.getCode());
            transactionItem.setStatus(TransactionStatusEnum.COMMIT.getCode());
            group.setStatus(TransactionStatusEnum.COMMIT.getCode());
        }
        group.setTransactionItemList(Collections.singletonList(transactionItem));
        request.setTransactionGroup(group);
        return request;
    }
}
