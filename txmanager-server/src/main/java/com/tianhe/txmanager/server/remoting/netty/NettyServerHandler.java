package com.tianhe.txmanager.server.remoting.netty;

import com.tianhe.txmanager.common.ExecutorServiceHelper;
import com.tianhe.txmanager.common.NettyManager;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.common.enums.ResultEnum;
import com.tianhe.txmanager.common.enums.RoleEnum;
import com.tianhe.txmanager.common.enums.TransactionStatusEnum;
import com.tianhe.txmanager.common.model.TransactionGroup;
import com.tianhe.txmanager.common.model.TransactionItem;
import com.tianhe.txmanager.common.model.TransactionRequest;
import com.tianhe.txmanager.common.utils.CommandHelper;
import com.tianhe.txmanager.common.utils.RemotingHelper;
import com.tianhe.txmanager.core.service.ManagerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: he.tian
 * @time: 2018-10-31 17:50
 */
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ManagerHandler managerHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransactionRequest request = (TransactionRequest) msg;
        TransactionGroup transactionGroup = request.getTransactionGroup();
        try {
            ActionEnum actionEnum = ActionEnum.get(request.getAction());
            logger.info("txManager 服务端接收到了 "+actionEnum.getName()+" 请求");
            switch (actionEnum){
                case HEART_BEAT:
                    executeHeartBeat(ctx, request);
                    break;
                case CREATE_TRANSACTION_GROUP:
                    executeCreateTransactionGroup(ctx, request);
                    break;
                case ADD_TRANSACTION:
                    executeAddTransaction(ctx, request, transactionGroup);
                    break;
                case GET_TRANSDACTION_GROUP_STATUS:
                    executeGetTransactionStatus(ctx, request, transactionGroup);
                    break;
                case FIND_TRANSACTION_GROUP:
                    executeGetTransactionGroupInfo(ctx, request, transactionGroup);
                    break;
                case ROLLBACK:
                    executeRollback(ctx, request);
                    break;
                case PRE_COMMIT:
                    executePreCommit(ctx,request);
                    break;
                case COMPLETE_COMMIT:
                    executeCommit(ctx,request);
                    break;
                default:
                    executeHeartBeat(ctx,request);
                    break;
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 提交完成后的处理，TODO 现在txManager支持单节点，如果后面是集群，这里最后还有去执行其他txManager中的事务组的事务信息提交完成后的处理
     * @param ctx
     * @param request
     */
    private void executeCommit(ChannelHandlerContext ctx, TransactionRequest request) {
        logger.info("txManager 服务端处理提交事务请求");
        List<TransactionItem> transactionItemList = request.getTransactionGroup().getTransactionItemList();
        TransactionItem transactionItem = transactionItemList.get(0);
        managerHandler.updateTransactionItem(request.getTransactionGroup().getGroupId(),transactionItem);
        transactionItem.setStatus(TransactionStatusEnum.COMMIT.getCode());
        request.setTaskId(request.getTaskId());
        request.setResult(ResultEnum.SUCCESS.getCode());
        request.setAction(ActionEnum.COMPLETE_COMMIT.getCode());
        ctx.writeAndFlush(request);
    }

    /**
     * 事务预提交，锁定资源，TODO 现在txManager支持单节点，如果后面是集群，这里最后还有去执行其他txManager中的事务组的事务信息提交处理
     * @param ctx
     * @param transactionRequest
     */
    private void executePreCommit(ChannelHandlerContext ctx, TransactionRequest transactionRequest) {
        logger.info("txManager 服务端处理预提交事务请求");
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.RECEIVE.getCode());
        request.setTaskId(transactionRequest.getTaskId());
        request.setResult(ResultEnum.SUCCESS.getCode());
        ctx.writeAndFlush(request);
        TransactionGroup transactionGroup = transactionRequest.getTransactionGroup();
        transactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
        TransactionGroup group = managerHandler.updateTransactionGroupStatus(transactionGroup);
        List<TransactionItem> transactionItemList = group.getTransactionItemList();
        List<TransactionItem> preCommitList = new ArrayList<>();
        for (TransactionItem transactionItem : transactionItemList) {
            if(RoleEnum.JOIN.getCode().equals(transactionItem.getRole())){
                preCommitList.add(transactionItem);
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(preCommitList.size());
        for (TransactionItem transactionItem : preCommitList) {
            ExecutorServiceHelper.INSTANCE.execute(new Runnable() {
                @Override
                public void run() {
                    Channel channel = null;
                    TransactionRequest preCommitRequest = CommandHelper.buildCommand(transactionItem, TransactionStatusEnum.COMMIT, channel);
                    if(channel.isActive()){
                        channel.writeAndFlush(preCommitRequest);
                    }else{
                        logger.error("txManager 服务端执行事务预提交失败");
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("txManager 服务端执行事务预提交失败，异常信息={}",e);
        }
    }

    /**
     * 事务回滚，这里只有事务组的发起方执行失败会对整个事务组的事务进行回滚，否则事务组事务执行失败会执行正向补偿处理，超过最大补偿次数需要人工干预
     * @param ctx
     * @param transactionRequest
     */
    private void executeRollback(ChannelHandlerContext ctx, TransactionRequest transactionRequest) {
        logger.info("txManager 服务端处理事务组事务回滚请求，事务组id={}",transactionRequest.getTransactionGroup().getGroupId());
        TransactionRequest request = new TransactionRequest();
        request.setResult(ResultEnum.SUCCESS.getCode());
        request.setTaskId(transactionRequest.getTaskId());
        request.setAction(ActionEnum.ROLLBACK.getCode());
//       事务回滚发起方本地事务已经回滚过，所以这里直接返回客户端
        ctx.writeAndFlush(transactionRequest);
//       更新事务组状态
        TransactionGroup transactionGroup = transactionRequest.getTransactionGroup();
        transactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        managerHandler.updateTransactionGroupStatus(transactionGroup);
        List<TransactionItem> transactionItemList = managerHandler.selectByTransactionGroupId(transactionGroup.getGroupId()).getTransactionItemList();
        List<TransactionItem> rollbackTransaqctionItemList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(transactionItemList)){
            for (TransactionItem transactionItem : transactionItemList) {
                if(RoleEnum.JOIN.getCode().equals(transactionItem.getRole())){
                    rollbackTransaqctionItemList.add(transactionItem);
                }
            }
        }

//        线程池去执行事务组中事务回滚 TODO 现在txManager支持单节点，如果后面是集群，这里最后还有去执行其他txManager中的事务组的事务信息回滚处理
        CountDownLatch countDownLatch = new CountDownLatch(rollbackTransaqctionItemList.size());
        for (TransactionItem transactionItem : rollbackTransaqctionItemList) {
            ExecutorServiceHelper.INSTANCE.execute(new Runnable() {
                @Override
                public void run() {
                    Channel channel = null;
                    TransactionRequest rollbackRequest = CommandHelper.buildCommand(transactionItem, TransactionStatusEnum.ROLLBACK,channel);
                    if(channel.isActive()){
                        channel.writeAndFlush(rollbackRequest);
                    }else{
                        logger.error("txManager 服务端回滚事务组事务失败，事务组id={}，事务id={}",transactionGroup.getGroupId(),transactionItem.getTaskId());
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("txManager 服务端回滚事务组事务失败，异常信息={}",e);
        }
    }

    /**
     * 心跳检测
     * @param ctx
     * @param transactionRequest
     */
    private void executeHeartBeat(ChannelHandlerContext ctx, TransactionRequest transactionRequest) {
        logger.info("txManager 服务端处理心跳检测请求");
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.HEART_BEAT.getCode());
        request.setTaskId(transactionRequest.getTaskId());
        ctx.writeAndFlush(request);
    }

    /**
     * 获取事务组信息
     * @param ctx
     * @param transactionRequest
     * @param transactionGroup
     */
    private void executeGetTransactionGroupInfo(ChannelHandlerContext ctx, TransactionRequest transactionRequest, TransactionGroup transactionGroup) {
        logger.info("txManager 服务端处理获取事务组信息请求");
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.FIND_TRANSACTION_GROUP.getCode());
        request.setTaskId(transactionRequest.getTaskId());
        request.setResult(ResultEnum.SUCCESS.getCode());
        TransactionGroup group = managerHandler.selectByTransactionGroupId(transactionGroup.getGroupId());
        request.setTransactionGroup(group);
        ctx.writeAndFlush(request);
    }

    /**
     * 获取事务组状态
     * @param ctx
     * @param transactionRequest
     * @param transactionGroup
     */
    private void executeGetTransactionStatus(ChannelHandlerContext ctx, TransactionRequest transactionRequest, TransactionGroup transactionGroup) {
        logger.info("txManager 服务端处理获取事务状态请求");
        TransactionRequest request = new TransactionRequest();
        String status = managerHandler.selectTransactionGroupStatus(transactionGroup.getGroupId());
        transactionGroup.setStatus(status);
        request.setTransactionGroup(transactionGroup);
        request.setResult(ResultEnum.SUCCESS.getCode());
        request.setTaskId(transactionRequest.getTaskId());
        request.setAction(ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode());
        ctx.writeAndFlush(request);
    }

    /**
     * 添加事务
     * @param ctx
     * @param transactionRequest
     * @param transactionGroup
     */
    private void executeAddTransaction(ChannelHandlerContext ctx, TransactionRequest transactionRequest, TransactionGroup transactionGroup) {
        logger.info("txManager 服务端处理添加事务请求");
        TransactionRequest request = new TransactionRequest();
        List<TransactionItem> transactionItemList = transactionGroup.getTransactionItemList();
        if(CollectionUtils.isNotEmpty(transactionItemList)){
            TransactionItem transactionItem = transactionItemList.get(0);
            transactionItem.setRemoteAddr(RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            managerHandler.addTransaction(transactionGroup,transactionItem);
        }
        request.setTransactionGroup(transactionGroup);
        request.setAction(ActionEnum.RECEIVE.getCode());
        request.setTaskId(transactionRequest.getTaskId());
        request.setResult(ResultEnum.SUCCESS.getCode());
        ctx.writeAndFlush(request);
    }

    /**
     * 创建事务组
     * @param ctx
     * @param transactionRequest
     */
    private void executeCreateTransactionGroup(ChannelHandlerContext ctx, TransactionRequest transactionRequest) {
        logger.info("txManager 服务端处理添加事务组请求");
        TransactionRequest request = new TransactionRequest();
        managerHandler.saveTransactionGroup(transactionRequest.getTransactionGroup());
        request.setTransactionGroup(transactionRequest.getTransactionGroup());
        request.setTaskId(transactionRequest.getTaskId());
        request.setResult(ResultEnum.SUCCESS.getCode());
        request.setAction(ActionEnum.RECEIVE.getCode());
        ctx.writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("txManager netty server出现异常");
        if(ctx.channel().isActive()){
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                ctx.close();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        是否达到了最大连接数
        if(NettyManager.getInstance().isAllowConnection()){
            NettyManager.getInstance().addChannel(ctx.channel());
        }else{
            logger.info("txManager netty server连接达到了最大连接数，连接关闭");
            ctx.close();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        NettyManager.getInstance().removeChannel(ctx.channel());
    }
}
