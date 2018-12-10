package com.tianhe.txmanager.client;

import com.tianhe.txmanager.client.handler.CompensationHandlerAdaptor;
import com.tianhe.txmanager.client.handler.JoinTransactionHandlerAdaptor;
import com.tianhe.txmanager.client.handler.StartTransactionHandlerAdaptor;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:49
 */
@Component
@Slf4j
public class TransactionInterceptor {

    @Autowired
    private StartTransactionHandlerAdaptor startTransactionHandlerAdaptor;

    @Autowired
    private TransactionClientHandler transactionClientHandler;

    @Autowired
    private CompensationHandlerAdaptor compensationHandlerAdaptor;

    @Autowired
    private JoinTransactionHandlerAdaptor joinTransactionHandlerAdaptor;

    public void intercept(Connection connection){
        String taskId = IdUtil.getTaskId();
        try {
//            如果事务组不存在就创建事务组，并添加发起者的事务信息
            if(Objects.isNull(ManagerContext.INSTANCE.getGroupId())){
                boolean saveTransactionGroup = startTransactionHandlerAdaptor.saveTransactionGroup(taskId);
                if(saveTransactionGroup){
                    boolean preCommit = startTransactionHandlerAdaptor.preCommit(ManagerContext.INSTANCE.getGroupId());
                    if(preCommit){
//                        提交本地事务
                        connection.commit();
                        startTransactionHandlerAdaptor.completeCommit(taskId);
                    }else{
                        connection.rollback();
                    }
                }
            }else{
//                如果事务组存在就添加参与者事务信息
                joinTransactionHandlerAdaptor.invoke();
            }
        } catch (Throwable e) {
//            事务提交失败，进行重试，超过最大重试次数后还是失败，通知事务管理器当前事务组中的事务进行回滚
//            通知事务管理器整个事务组的事务进行回滚
            try {
                connection.rollback();
            } catch (SQLException ex) {
                log.error("本地事务提交失败，异常信息={}",ex);
            }
            startTransactionHandlerAdaptor.rollback();
        }finally {
           startTransactionHandlerAdaptor.release(taskId);
        }
    }
}
