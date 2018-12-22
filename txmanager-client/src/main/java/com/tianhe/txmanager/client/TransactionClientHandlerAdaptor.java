package com.tianhe.txmanager.client;

import com.tianhe.txmanager.client.remoting.netty.NettyClientHandler;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:45
 */
@Service
public class TransactionClientHandlerAdaptor implements TransactionClientHandler {

    @Autowired
    private NettyClientHandler clientHandler;

    @Override
    public boolean createTransactionGroup(TransactionGroup transactionGroup) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.CREATE_TRANSACTION_GROUP.getCode());
        request.setTransactionGroup(transactionGroup);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (boolean) send;
        }
        return false;
    }

    @Override
    public boolean addTransaction(String transactionGroupId, TransactionItem transactionItem) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.ADD_TRANSACTION.getCode());
        TransactionGroup group = new TransactionGroup();
        group.getTransactionItemList().add(transactionItem);
        request.setTransactionGroup(group);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (boolean) send;
        }
        return false;
    }

    @Override
    public String findTransactionGroupStatus(String transactionGroupId) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(transactionGroupId);
        request.setTransactionGroup(group);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (String) send;
        }
        return TransactionStatusEnum.ROLLBACK.getCode();
    }

    @Override
    public TransactionGroup findTransactionGroup(String transactionGroupId) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.FIND_TRANSACTION_GROUP.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(transactionGroupId);
        request.setTransactionGroup(group);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (TransactionGroup) send;
        }
        return null;
    }

    @Override
    public void rollbackTransactionGroup(String transactionGroupId) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.ROLLBACK.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        group.setGroupId(transactionGroupId);
        request.setTransactionGroup(group);
        clientHandler.send(request);
    }

    @Override
    public boolean preCommit(String transactionGroupId) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.PRE_COMMIT.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(transactionGroupId);
        request.setTransactionGroup(group);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (boolean) send;
        }
        return false;
    }

    @Override
    public boolean commit(String transactionGroupId, String taskId, String status) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.COMPLETE_COMMIT.getCode());
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(transactionGroupId);
        TransactionItem item = new TransactionItem();
        item.setTaskId(taskId);
        item.setStatus(status);
        group.getTransactionItemList().add(item);
        request.setTransactionGroup(group);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (boolean) send;
        }
        return false;
    }

    @Override
    public String findTransactionExist(TransactionRequest request) {
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (String) send;
        }
        return "";
    }

    @Override
    public boolean saveTransactionGroupId(Long threadNo, String groupId) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.SAVE_TRANSACTION_GROUP.getCode());
        request.setThreadNo(threadNo);
        TransactionGroup group = new TransactionGroup();
        group.setGroupId(groupId);
        request.setTransactionGroup(group);
        Object send = clientHandler.send(request);
        if(Objects.nonNull(send)){
            return (boolean) send;
        }
        return false;
    }
}
