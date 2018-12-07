package com.tianhe.txmanager.remoting.netty;

import com.tianhe.txmanager.common.ScheduleExecutorServiceHelper;
import com.tianhe.txmanager.common.Task;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.ResultEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.SpringHelper;
import com.tianhe.txmanager.remoting.config.ClientConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:44
 */
@Component
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    @Autowired
    private ClientConfig clientConfig;

    @Getter
    private volatile ChannelHandlerContext ctx;

    @Autowired
    private SpringHelper springHelper;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       TransactionRequest request = (TransactionRequest) msg;
       ActionEnum actionEnum = ActionEnum.get(request.getAction());
       log.info("netty客户端接收到了={}请求",actionEnum.getName());
       try {
           switch (actionEnum){
               case HEART_BEAT:
                   break;
               case RECEIVE:
                   receive(request);
                   break;
               case ROLLBACK:
                   execute(request);
                   break;
               case COMMIT:
                   execute(request);
                   break;
               case GET_TRANSDACTION_GROUP_STATUS:
                   getTransactionGroupStatus(request);
                   break;
               case FIND_TRANSACTION_GROUP:
                   findTransactionInfo(request);
                   break;
               default:
                       break;
           }
       }finally {
           ReferenceCountUtil.release(msg);
       }
    }

    /**
     * 获取事务组信息
     * @param request
     */
    private void findTransactionInfo(TransactionRequest request) {
        Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
        task.setResult(request.getTransactionGroup());
        task.singal();
    }

    /**
     * 获取事务组状态
     * @param request
     */
    private void getTransactionGroupStatus(TransactionRequest request) {
        Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
        TransactionGroup transactionGroup = request.getTransactionGroup();
        task.setResult(transactionGroup.getStatus());
        task.singal();
    }

    /**
     * 事务回滚
     * @param request
     */
    private void execute(TransactionRequest request) {
        List<TransactionItem> transactionItemList = request.getTransactionGroup().getTransactionItemList();
        if(CollectionUtils.isNotEmpty(transactionItemList)){
            TransactionItem transactionItem = transactionItemList.get(0);
            Task task = ManagerContext.INSTANCE.getTask(transactionItem.getTaskId());
            task.setResult(transactionItem.getStatus());
            task.singal();
        }
    }

    /**
     * 接受请求
     * @param transactionRequest
     */
    private void receive(TransactionRequest transactionRequest) {
        Task task = ManagerContext.INSTANCE.getTask(transactionRequest.getTaskId());
        task.setResult(ResultEnum.SUCCESS.getCode().equals(transactionRequest.getResult()));
        task.singal();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                springHelper.getBean(NettyClient.class).doConnect();
            }else if (event.state() == IdleState.WRITER_IDLE){
                TransactionRequest transactionRequest = new TransactionRequest();
                transactionRequest.setAction(ActionEnum.HEART_BEAT.getCode());
                ctx.writeAndFlush(transactionRequest);
                logger.info("txManager 发送事务管理器心跳检测");
            }else if(event.state() == IdleState.ALL_IDLE){
                springHelper.getBean(NettyClient.class).doConnect();
            }
        }
    }

    public Object send(TransactionRequest request){
        Object result = null;
        if(ctx != null && ctx.channel() != null && ctx.channel().isActive()){
            Task task = ManagerContext.INSTANCE.getTask(IdUtil.getTaskId());
            ctx.writeAndFlush(request);
//            超时处理
            ScheduledFuture<?> schedule = ScheduleExecutorServiceHelper.INSTANCE.schedule(new Runnable() {
                @Override
                public void run() {
                    if (!task.isNotify()) {
                        if (ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode().equals(request.getAction())) {
                            task.setResult(ResultEnum.TIMEOUT.getCode());
                        } else if (ActionEnum.FIND_TRANSACTION_GROUP.getCode().equals(request.getAction())) {
                            task.setResult(null);
                        } else {
                            task.setResult(false);
                        }
                        task.singal();
                    }
                }
            }, clientConfig.getDelayTime(), TimeUnit.SECONDS);
//            分布式事务线程阻塞
            task.await();

            if(!schedule.isDone()){
                schedule.cancel(false);
            }

            result = task.getResult();
            ManagerContext.INSTANCE.getTaskMap().remove(task.getTaskId());
        }
        return result;
    }
}
