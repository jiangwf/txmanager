package com.tianhe.txmanager.client;

import com.tianhe.txmanager.client.anotation.DistributedTransation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:49
 */
@Aspect
@Component
@Slf4j
public class TransactionAspect {

    @Autowired
    private TransactionService transactionService;

    @Before(value="@annotation(com.tianhe.txmanager.client.anotation.DistributedTransation)")
    public void executeTransaction(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        log.info("分布式事务 执行方法={}",methodName);
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        try {
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
            DistributedTransation distributedTransation = method.getAnnotation(DistributedTransation.class);
//            TODO

        } catch (NoSuchMethodException e) {
            log.error("分布式事务 切面找不到具体的方法，异常信息={}",e);
        }
    }
}
