package com.tianhe.txmanager.example.test;

import com.tianhe.txmanager.client.remoting.netty.NettyClient;
import com.tianhe.txmanager.client.remoting.netty.NettyClientHandler;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.common.utils.IdUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-12-18 17:21
 */
public class RemotingTest extends AppTest{

    @Autowired
    private NettyClientHandler clientHandler;

    @Autowired
    private NettyClient nettyClient;

    @Test
    public void execute(){
        nettyClient.start();
        Object send = clientHandler.send(buildRequestCreateTransactionGroup());
        System.out.println(send);
        try {
            nettyClient.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TransactionRequest buildRequestHeartBeat(){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.HEART_BEAT.getCode());

        TransactionGroup group = new TransactionGroup();
        request.setTransactionGroup(group);
        return request;
    }

    private TransactionRequest buildRequestCreateTransactionGroup(){
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.CREATE_TRANSACTION_GROUP.getCode());

        TransactionGroup group = new TransactionGroup();
        group.setGroupId(IdUtil.getTransactionGroupId());

        TransactionItem item = new TransactionItem();
        item.setCreateDate(new Date());
        item.setTransactionGroupId(group.getGroupId());
        item.setRole(RoleEnum.GROUP.getCode());
        item.setTaskId(IdUtil.getTaskId());
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
}
