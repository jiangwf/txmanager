package com.tianhe.txmanager.client.remoting.netty;

import com.tianhe.txmanager.client.config.ClientConfig;
import com.tianhe.txmanager.common.ScheduleExecutorServiceHelper;
import com.tianhe.txmanager.common.Task;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.ResultEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.common.utils.CollectionUtil;
import com.tianhe.txmanager.common.utils.IdUtil;
import com.tianhe.txmanager.core.ManagerContext;
import com.tianhe.txmanager.core.SpringHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.Setter;
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
@ChannelHandler.Sharable
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
        logger.info("txManager netty客户端接收到了 {} 请求",actionEnum.getName());
       try {
           switch (actionEnum){
               case HEART_BEAT:
                   Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
                   task.setResult(ResultEnum.SUCCESS.getCode());
                   task.singal();
                   break;
               case RECEIVE:
                   receive(request);
                   break;
               case ROLLBACK:
                   execute(request);
                   break;
               case COMPLETE_COMMIT:
                   execute(request);
                   break;
               case GET_TRANSDACTION_GROUP_STATUS:
                   getTransactionGroupStatus(request);
                   break;
               case FIND_TRANSACTION_GROUP:
                   findTransactionInfo(request);
                   break;
               case FIND_TRANSACTION_EXIST:
                   findTransactionExists(request);
                   break;
               default:
                       break;
           }
       }finally {
           ReferenceCountUtil.release(msg);
       }
    }

    private void findTransactionExists(TransactionRequest request) {
        logger.info("txManager client处理查找事务组是否存在请求");
        Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
        task.setResult(request.getResult());
        task.singal();
    }

    /**
     * 获取事务组信息
     * @param request
     */
    private void findTransactionInfo(TransactionRequest request) {
        logger.info("txManager client处理查找事务信息请求");
        Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
        task.setResult(request.getTransactionGroup());
        task.singal();
    }

    /**
     * 获取事务组状态
     * @param request
     */
    private void getTransactionGroupStatus(TransactionRequest request) {
        logger.info("txManager client处理查找事务组状态请求");
        Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
        TransactionGroup transactionGroup = request.getTransactionGroup();
        task.setResult(transactionGroup.getStatus());
        task.singal();
    }

    /**
     * 事务提交或回滚
     * @param request
     */
    private void execute(TransactionRequest request) {
        logger.info("txManager client处理事务提交或回滚请求");
        List<TransactionItem> transactionItemList = request.getTransactionGroup().getTransactionItemList();
        if(CollectionUtil.isNotEmpty(transactionItemList)){
            TransactionItem transactionItem = transactionItemList.get(0);
            Task task = ManagerContext.INSTANCE.getTask(request.getTaskId());
            task.setResult(transactionItem.getStatus());
            task.singal();
        }
    }

    /**
     * 接受请求
     * @param transactionRequest
     */
    private void receive(TransactionRequest transactionRequest) {
        logger.info("txManager client处理接收请求");
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
                logger.info("txManager 客户端发送事务管理器心跳检测");
            }else if(event.state() == IdleState.ALL_IDLE){
                springHelper.getBean(NettyClient.class).doConnect();
            }
        }
    }

    public Object send(TransactionRequest request){
        logger.info("txManager client发送事务请求");
        Object result = null;
        if(ctx != null && ctx.channel() != null && ctx.channel().isActive()){
            Task task = ManagerContext.INSTANCE.getTask(IdUtil.getTaskId()); //TODO 这里会出现内存泄漏，后续优化
            if((ActionEnum.PRE_COMMIT.getCode().equals(request.getAction())) || (ActionEnum.ROLLBACK.getCode().equals(request.getAction()))){
                request.setTaskId(request.getTransactionGroup().getTransactionItemList().get(0).getTaskId());
            }else{
                request.setTaskId(task.getTaskId());
            }
            ctx.writeAndFlush(request);
//            超时处理
            ScheduledFuture<?> schedule = ScheduleExecutorServiceHelper.INSTANCE.schedule(new Runnable() {
                @Override
                public void run() {
                    if (!task.isNotify()) {
                        if (ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode().equals(request.getAction())) {
                            task.setResult(ResultEnum.TIME_OUT.getCode());
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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("netty client 连接被异常关闭了，现在重连");
        springHelper.getBean(NettyClient.class).doConnect();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("txManager netty client出现异常");
        if(ctx.channel().isActive()){
            ctx.close();
        }
    }
}
