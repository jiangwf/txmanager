package com.tianhe.txmanager.client;

import com.tianhe.txmanager.client.annotation.Transaction;
import com.tianhe.txmanager.client.handler.CompensationHandlerAdaptor;
import com.tianhe.txmanager.client.handler.StartTransactionHandlerAdaptor;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.core.ManagerContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 *  TODO 客户端需要开启aspect
 * @author: he.tian
 * @time: 2018-11-26 17:49
 */
@Aspect
@Component
@Slf4j
public class TransactionAspect {

    @Autowired
    private StartTransactionHandlerAdaptor startTransactionHandlerAdaptor;

    @Autowired
    private TransactionClientHandler transactionOperation;

    @Autowired
    private ManagerContext managerContext;

    @Autowired
    private CompensationHandlerAdaptor compensationHandler;

    @Before(value="@annotation(com.tianhe.txmanager.client.annotation.Transaction)")
    public Object executeTransaction(ProceedingJoinPoint proceedingJoinPoint){
        String methodName = proceedingJoinPoint.getSignature().getName();
        log.info("分布式事务 执行方法={}",methodName);
        Method proxyMethod = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        Object result = null;
        try {
            Method method = proceedingJoinPoint.getTarget().getClass().getDeclaredMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
            Transaction transaction = method.getAnnotation(Transaction.class);
            TransactionGroup group = managerContext.getTransactionGroupMap().get(Thread.currentThread());
//            如果事务组不存在就创建事务组，并添加发起者的事务信息
            if(Objects.isNull(group)){
                log.info("开启分布式事务，方法名={}",method.getName());
                TransactionItem item = (TransactionItem) startTransactionHandlerAdaptor.invoke();
                group.getTransactionItemList().add(item);
                result = proceedingJoinPoint.proceed();
//                提交事务
                transactionOperation.commit(managerContext.getTransactionGroupMap().get(Thread.currentThread()).getGroupId(), item.getTaskId(), TransactionStatusEnum.COMMIT.getCode());
                log.info("事务提交完毕");
            }else{
//                如果事务组存在就添加参与者事务信息
                log.info("参与分布式事务，方法名={}",method.getName());
                startTransactionHandlerAdaptor.invoke();
            }
        } catch (Throwable e) {
//            事务提交失败，进行重试，超过最大重试次数后还是失败，通知事务管理器当前事务组中的事务进行回滚
//            本地补偿
//            通知事务管理器整个事务组的事务进行回滚
            transactionOperation.rollbackTransactionGroup(managerContext.getTransactionGroupMap().get(Thread.currentThread()).getGroupId());
            log.error("分布式事务 事务执行失败，异常信息={}",e);
            try {
//                异常抛出到上层，上层进行当前事务回滚
                throw e;
            } catch (Throwable throwable) {
                log.error("分布式事务 处理失败，异常信息={}",e);
            }
        }
        return result;
    }
}
