package com.tianhe.txmanager.client;

import com.tianhe.txmanager.client.config.ClientConfig;
import com.tianhe.txmanager.client.handler.TransactionHandlerAdaptor;
import com.tianhe.txmanager.common.ScheduleExecutorServiceHelper;
import com.tianhe.txmanager.common.Task;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.ResultEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.common.utils.StringUtil;
import com.tianhe.txmanager.core.ManagerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:49
 */
@Component
public class TransactionController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TransactionHandlerAdaptor transactionHandlerAdaptor;

    @Autowired
    private ClientConfig clientConfig;

//    TODO 有些细节还没有覆盖到
    public void execute(Connection connection) {
        Long threadNo = Thread.currentThread().getId();
        String transactionGroupId = IdUtil.getTransactionGroupId();
        String taskId = IdUtil.getTaskId();

//      如果事务组不存在就创建事务组，并添加发起者的事务信息
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.FIND_TRANSACTION_EXIST.getCode());
        request.setThreadNo(threadNo);
        String groupId = transactionHandlerAdaptor.findTransactionExist(request);

        if (StringUtil.isEmpty(groupId)) {
            try {
                boolean saveTransactionGroup = transactionHandlerAdaptor.saveTransactionGroup(threadNo,taskId, transactionGroupId);
                logger.info("txManager 创建事务组，事务组id={},taskId={},执行结果={}", transactionGroupId, taskId, saveTransactionGroup);
                if (saveTransactionGroup) {
//                  这里进行事务的预提交处理
                    boolean preCommit = transactionHandlerAdaptor.preCommit(groupId);
                    if(preCommit){
//                      提交本地事务
                        connection.commit();
                        logger.info("txManager 提交事务，事务组id={}，事务id={}", transactionGroupId, taskId);
                        boolean completeCommit = transactionHandlerAdaptor.completeCommit(threadNo,taskId);
                        logger.info("txManager 通知事务管理器事务提交，事务组id={},事务id={},执行结果={}", transactionGroupId, taskId, completeCommit);
                    }
                }
            } catch (Throwable e) {
//              TODO 事务提交失败，进行重试，超过最大重试次数后还是失败，通知事务管理器当前事务组中的事务进行回滚
//              通知事务管理器整个事务组的事务进行回滚
                try {
                    connection.rollback();
                    logger.info("txManager 事务提交失败，回滚，事务组id={}，事务id={}", transactionGroupId, taskId);
                } catch (SQLException ex) {
                    logger.error("txManager 事务提交失败，事务组id={}，事务id={},异常信息={}", transactionGroupId, taskId, ex);
                }
                transactionHandlerAdaptor.rollbackTransactionGroup(groupId);
                logger.info("txManager 回滚事务组事务，事务id={}", transactionGroupId);
//                TODO 成功或者失败的事务组信息未做处理
            } finally {
                transactionHandlerAdaptor.release(taskId);
                logger.info("txManager 事务执行完毕，删除事务组id={}，事务id={}", transactionGroupId, taskId);
            }
        } else {
            Task task = ManagerContext.INSTANCE.getTask(taskId);
            try {
//              如果事务组存在就添加参与者事务信息
//              添加事务组信息
                boolean addTransaction = transactionHandlerAdaptor.addTransaction(threadNo,taskId);
                logger.info("txManager 添加事务，事务组id={},事务id={}", transactionGroupId, taskId);
                if (addTransaction) {
//                  这里进行事务的预提交处理
                    boolean preCommit = transactionHandlerAdaptor.preCommit(groupId);
                    if(preCommit){
//                      客户端netty连接超时处理
                        ScheduledFuture<?> schedule = ScheduleExecutorServiceHelper.INSTANCE.schedule(new Runnable() {
                            @Override
                            public void run() {
                                if (!task.isNotify()) {
                                    String transactionGroupStatus = transactionHandlerAdaptor.findTransactionGroupStatus(ManagerContext.INSTANCE.getGroupId(threadNo));
                                    if (TransactionStatusEnum.COMMIT.getCode().equals(transactionGroupStatus)) {
//                                  如果事务组状态是预提交或提交状态，本地事务状态设置为提交
                                        task.setResult(TransactionStatusEnum.COMMIT.getCode());
                                        task.singal();
                                    } else {
                                        task.setResult(ResultEnum.TIME_OUT.getCode());
                                        task.singal();
                                    }
                                }
                            }
                        }, clientConfig.getDelayTime(), TimeUnit.SECONDS);
                        task.await();

//                      如果客户端正常返回了就不需要执行定时任务了
                        if (!schedule.isDone()) {
                            schedule.cancel(false);
                        }
                        if (TransactionStatusEnum.COMMIT.getCode().equals(task.getResult())) {
//                          提交本地事务
                            connection.commit();
                            logger.info("txManager 提交事务，事务组id={},事务id={}", transactionGroupId, taskId);
//                          通知txManager 事务提交
                            boolean completeCommit = transactionHandlerAdaptor.completeCommit(threadNo,taskId);
                            logger.info("txManager 通知事务管理器，事务提交，事务组id={}，事务id={}，执行结果={}", transactionGroupId, taskId, completeCommit);

                        } else {
                            connection.rollback();
                            logger.info("txManager 事务回滚，事务组id={}，事务id={}", transactionGroupId, taskId);
                            boolean rollbackTransactionItem = transactionHandlerAdaptor.rollbackTransactionItem(threadNo,taskId);
                            logger.info("txManager 事务回滚，事务组id={}，事务id={},执行结果={}", transactionGroupId, taskId, rollbackTransactionItem);
                        }
                    }
                }
            } catch (Throwable throwable) {
                try {
                    connection.rollback();
                    logger.info("txManager 事务提交失败，事务回滚，事务组id={},事务id={}", transactionGroupId, taskId);
                } catch (SQLException e) {
                    logger.error("本地事务回滚失败，事务组id={}，事务id={},异常信息={}", transactionGroupId, taskId, e);
                }
//              通知txManager 事务失败
                boolean fail = transactionHandlerAdaptor.fail(threadNo,taskId);
                logger.info("txManager 同事事务管理器事务失败，事务组id={}，事务id={}，执行结果={}", transactionGroupId, taskId, fail);
            } finally {
                ManagerContext.INSTANCE.getTaskMap().remove(taskId);
                logger.info("txManager 事务执行完毕,删除taskId={}", taskId);
            }
        }
    }
}
