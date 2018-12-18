package com.tianhe.txmanager.client;

import com.tianhe.txmanager.client.config.ClientConfig;
import com.tianhe.txmanager.client.handler.TransactionHandlerAdaptor;
import com.tianhe.txmanager.common.ScheduleExecutorServiceHelper;
import com.tianhe.txmanager.common.Task;
import com.tianhe.txmanager.common.enums.ResultEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:49
 */
@Component
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionHandlerAdaptor transactionHandlerAdaptor;

    @Autowired
    private ClientConfig clientConfig;

    public void intercept(Connection connection) {
        String transactionGroupId = IdUtil.getTransactionGroupId();
        String taskId = IdUtil.getTaskId();
//      如果事务组不存在就创建事务组，并添加发起者的事务信息
        if (Objects.isNull(ManagerContext.INSTANCE.getGroupId())) {
            try {
                boolean saveTransactionGroup = transactionHandlerAdaptor.saveTransactionGroup(taskId, transactionGroupId);
                log.info("txManager 创建事务组，事务组id={},taskId={},执行结果={}", transactionGroupId, taskId, saveTransactionGroup);
                if (saveTransactionGroup) {
//                  提交本地事务
                    connection.commit();
                    log.info("txManager 提交事务，事务组id={}，事务id={}", transactionGroupId, taskId);
                    boolean completeCommit = transactionHandlerAdaptor.completeCommit(taskId);
                    log.info("txManager 通知事务管理器事务提交，事务组id={},事务id={},执行结果={}", transactionGroupId, taskId, completeCommit);

                }
            } catch (Throwable e) {
//              事务提交失败，进行重试，超过最大重试次数后还是失败，通知事务管理器当前事务组中的事务进行回滚 TODO
//              通知事务管理器整个事务组的事务进行回滚
                try {
                    connection.rollback();
                    log.info("txManager 事务提交失败，回滚，事务组id={}，事务id={}", transactionGroupId, taskId);
                } catch (SQLException ex) {
                    log.error("txManager 事务提交失败，事务组id={}，事务id={},异常信息={}", transactionGroupId, taskId, ex);
                }
                transactionHandlerAdaptor.rollbackTransactionGroup();
                log.info("txManager 回滚事务组事务，事务id={}", transactionGroupId);
            } finally {
                transactionHandlerAdaptor.release(taskId);
                log.info("txManager 事务执行完毕，删除事务组id={}，事务id={}", transactionGroupId, taskId);
            }
        } else {
            Task task = ManagerContext.INSTANCE.getTask(taskId);
            try {
//                如果事务组存在就添加参与者事务信息
//                添加事务组信息
                boolean addTransaction = transactionHandlerAdaptor.addTransaction(taskId);
                log.info("txManager 添加事务，事务组id={},事务id={}", transactionGroupId, taskId);
                if (addTransaction) {
//                  客户端netty连接超时处理
                    ScheduledFuture<?> schedule = ScheduleExecutorServiceHelper.INSTANCE.schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (!task.isNotify()) {
                                String transactionGroupStatus = transactionHandlerAdaptor.findTransactionGroupStatus(ManagerContext.INSTANCE.getGroupId());
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
                    task.wait();

//                  如果客户端正常返回了就不需要执行定时任务了
                    if (!schedule.isDone()) {
                        schedule.cancel(false);
                    }
                    if (TransactionStatusEnum.COMMIT.getCode().equals(task.getResult())) {
//                      提交本地事务
                        connection.commit();
                        log.info("txManager 提交事务，事务组id={},事务id={}", transactionGroupId, taskId);
//                      通知txManager 事务提交
                        boolean completeCommit = transactionHandlerAdaptor.completeCommit(taskId);
                        log.info("txManager 通知事务管理器，事务提交，事务组id={}，事务id={}，执行结果={}", transactionGroupId, taskId, completeCommit);

                    } else {
                        connection.rollback();
                        log.info("txManager 事务回滚，事务组id={}，事务id={}", transactionGroupId, taskId);
                        boolean rollbackTransactionItem = transactionHandlerAdaptor.rollbackTransactionItem(taskId);
                        log.info("txManager 事务回滚，事务组id={}，事务id={},执行结果={}", transactionGroupId, taskId, rollbackTransactionItem);
                    }
                }
            } catch (Throwable throwable) {
                try {
                    connection.rollback();
                    log.info("txManager 事务提交失败，事务回滚，事务组id={},事务id={}", transactionGroupId, taskId);
                } catch (SQLException e) {
                    log.error("本地事务回滚失败，事务组id={}，事务id={},异常信息={}", transactionGroupId, taskId, e);
                }
//              通知txManager 事务失败
                boolean fail = transactionHandlerAdaptor.fail(taskId);
                log.info("txManager 同事事务管理器事务失败，事务组id={}，事务id={}，执行结果={}", transactionGroupId, taskId, fail);
            } finally {
                ManagerContext.INSTANCE.getTaskMap().remove(taskId);
                log.info("txManager 事务执行完毕,删除taskId={}", taskId);
            }
        }
    }
}
