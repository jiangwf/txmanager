package com.tianhe.txmanager.example;

import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.common.utils.IdUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-12-19 15:47
 */
public class RequestHelper {

    public static TransactionRequest buildRequestHeartBeat(){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.HEART_BEAT.getCode());

        TransactionGroup group = new TransactionGroup();
        request.setTransactionGroup(group);
        return request;
    }

    public static TransactionRequest buildRequestCreateTransactionGroup(){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.CREATE_TRANSACTION_GROUP.getCode());

        TransactionGroup group = new TransactionGroup();
        group.setGroupId(IdUtil.getTransactionGroupId());

        TransactionItem item = new TransactionItem();
        item.setCreateDate(new Date());
        item.setTransactionGroupId(group.getGroupId());
        item.setRole(RoleEnum.GROUP.getCode());
        item.setTaskId(IdUtil.getTransactionId());
        try {
            item.setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        List<TransactionItem> transactionItemList = new ArrayList<>();
        transactionItemList.add(item);
        group.setTransactionItemList(transactionItemList);
        request.setTransactionGroup(group);
        return request;
    }

    //TODO 执行类、方法没有添加
    public static TransactionRequest buildRequestAddTransaction(String groupId){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.ADD_TRANSACTION.getCode());

        TransactionGroup group = new TransactionGroup();
        group.setGroupId(groupId);
        group.setStatus(TransactionStatusEnum.BEGIN.getCode());

        TransactionItem item = new TransactionItem();
        item.setStatus(TransactionStatusEnum.BEGIN.getCode());
        item.setCreateDate(new Date());
        try {
            item.setRemoteAddr(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        item.setRole(RoleEnum.JOIN.getCode());
        item.setTaskId(IdUtil.getTransactionId());

        group.getTransactionItemList().add(item);
        request.setTransactionGroup(group);
        return request;
    }

    public static TransactionRequest buildRequestFindTransactionGroupStatus(String groupId){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(groupId);
        request.setTransactionGroup(group);
        return request;
    }

    public static TransactionRequest buildRequestFindTransactionGroup(String groupId){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.FIND_TRANSACTION_GROUP.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(groupId);
        request.setTransactionGroup(group);
        return request;
    }

    public static TransactionRequest buildRequestRollback(String groupId){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.ROLLBACK.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        group.setGroupId(groupId);
        request.setTransactionGroup(group);
        return request;
    }

    public static TransactionRequest buildRequestPrecommit(String groupId){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.PRE_COMMIT.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(groupId);
        request.setTransactionGroup(group);
        return request;
    }

    public static TransactionRequest buildRequestCommit(String groupId,String itemId){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.COMPLETE_COMMIT.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(groupId);

        TransactionItem item = new TransactionItem();
        item.setTaskId(itemId);
        item.setStatus(TransactionStatusEnum.COMMIT.getCode());
        group.getTransactionItemList().add(item);
        request.setTransactionGroup(group);
        return request;
    }
}
