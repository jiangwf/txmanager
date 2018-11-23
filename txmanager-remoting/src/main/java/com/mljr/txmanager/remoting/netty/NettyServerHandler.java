package com.mljr.txmanager.remoting.netty;

import com.mljr.txmanager.common.CommandHelper;
import com.mljr.txmanager.common.ManagerConfig;
import com.mljr.txmanager.common.NettyManager;
import com.mljr.txmanager.common.enums.ActionEnum;
import com.mljr.txmanager.common.enums.ResultEnum;
import com.mljr.txmanager.common.enums.RoleEnum;
import com.mljr.txmanager.common.enums.TransactionStatusEnum;
import com.mljr.txmanager.common.model.TransactionGroup;
import com.mljr.txmanager.common.model.TransactionItem;
import com.mljr.txmanager.common.model.TransactionRequest;
import com.mljr.txmanager.common.utils.IdUtil;
import com.mljr.txmanager.common.utils.RemotingHelper;
import com.mljr.txmanager.core.service.ManagerService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author: he.tian
 * @time: 2018-10-31 17:50
 */
@Component
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ManagerConfig managerConfig;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransactionRequest request = (TransactionRequest) msg;
        TransactionGroup transactionGroup = request.getTransactionGroup();
        try {
            ActionEnum actionEnum = ActionEnum.get(request.getAction());
            log.info("服务端接收到了 "+actionEnum.getName()+" 请求");
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
                case COMMIT:
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
        List<TransactionItem> transactionItemList = request.getTransactionGroup().getTransactionItemList();
        TransactionItem transactionItem = transactionItemList.get(0);
        managerService.updateTransactionItem(request.getTransactionGroup().getTransactionId(),transactionItem);
    }

    /**
     * 事务预提交，锁定资源，TODO 现在txManager支持单节点，如果后面是集群，这里最后还有去执行其他txManager中的事务组的事务信息提交处理
     * @param ctx
     * @param transactionRequest
     */
    private void executePreCommit(ChannelHandlerContext ctx, TransactionRequest transactionRequest) {
        TransactionRequest request = new TransactionRequest();
        request.setAction(ActionEnum.RECEIVE.getCode());
        request.setRequestId(transactionRequest.getRequestId());
        request.setResult(ResultEnum.SUCCESS.getCode());
        ctx.writeAndFlush(request);
        TransactionGroup transactionGroup = transactionRequest.getTransactionGroup();
        transactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
        managerService.updateTransactionGroupStatus(transactionGroup);
        List<TransactionItem> transactionItemList = managerService.selectByTransactionGroupId(transactionGroup.getTransactionId());
        List<TransactionItem> preCommitList = new ArrayList<>();
        for (TransactionItem transactionItem : transactionItemList) {
            if(RoleEnum.JOIN.getCode().equals(transactionItem.getRole())){
                preCommitList.add(transactionItem);
            }
        }
        ExecutorService executeService = managerConfig.getExecuteService();
        CountDownLatch countDownLatch = new CountDownLatch(preCommitList.size());
        for (TransactionItem transactionItem : preCommitList) {
            executeService.execute(new Runnable() {
                @Override
                public void run() {
                    Channel channel = null;
                    TransactionRequest preCommitRequest = CommandHelper.buildCommand(transactionItem, TransactionStatusEnum.COMMIT, channel);
                    if(channel.isActive()){
                        channel.writeAndFlush(preCommitRequest);
                    }else{
                        log.error("事务管理器执行事务提交失败");
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("事务管理器执行事务提交失败，异常信息={}",e);
        }
    }

    /**
     * 事务回滚，这里只有事务组的发起方执行失败会对整个事务组的事务进行回滚，否则事务组事务执行失败会执行正向补偿处理，超过最大补偿次数需要人工干预
     * @param ctx
     * @param request
     */
    private void executeRollback(ChannelHandlerContext ctx, TransactionRequest request) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setResult(ResultEnum.SUCCESS.getCode());
        transactionRequest.setRequestId(request.getRequestId());
        transactionRequest.setAction(ActionEnum.ROLLBACK.getCode());
//       事务回滚发起方本地事务已经回滚过，所以这里直接返回客户端
        ctx.writeAndFlush(request);
//       更新事务组状态
        TransactionGroup transactionGroup = request.getTransactionGroup();
        transactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        managerService.updateTransactionGroupStatus(transactionGroup);
        log.info("事务组id={}需要做回滚处理",transactionGroup.getTransactionId());
        List<TransactionItem> transactionItemList = managerService.selectByTransactionGroupId(transactionGroup.getTransactionId());
        List<TransactionItem> rollbackTransaqctionItemList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(transactionItemList)){
            for (TransactionItem transactionItem : transactionItemList) {
                if(RoleEnum.JOIN.getCode().equals(transactionItem.getRole())){
                    rollbackTransaqctionItemList.add(transactionItem);
                }
            }
        }

//        线程池去执行事务组中事务回滚 TODO 现在txManager支持单节点，如果后面是集群，这里最后还有去执行其他txManager中的事务组的事务信息回滚处理
        ExecutorService executeService = managerConfig.getExecuteService();
        CountDownLatch countDownLatch = new CountDownLatch(rollbackTransaqctionItemList.size());
        for (TransactionItem transactionItem : rollbackTransaqctionItemList) {
            executeService.execute(new Runnable() {
                @Override
                public void run() {
                    Channel channel = null;
                    TransactionRequest rollbackRequest = CommandHelper.buildCommand(transactionItem, TransactionStatusEnum.ROLLBACK,channel);
                    if(channel.isActive()){
                        channel.writeAndFlush(rollbackRequest);
                    }else{
                        log.error("事务管理器回滚失败，事务组id={}，事务id={}",transactionGroup.getTransactionId(),transactionItem.getTaskId());
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("事务组事务回滚失败，异常信息={}",e);
        }
    }

    /**
     * 心跳检测
     * @param ctx
     * @param request
     */
    private void executeHeartBeat(ChannelHandlerContext ctx, TransactionRequest request) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAction(ActionEnum.HEART_BEAT.getCode());
        transactionRequest.setRequestId(request.getRequestId());
        ctx.writeAndFlush(request);
    }

    /**
     * 获取事务组信息
     * @param ctx
     * @param request
     * @param transactionGroup
     */
    private void executeGetTransactionGroupInfo(ChannelHandlerContext ctx, TransactionRequest request, TransactionGroup transactionGroup) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAction(ActionEnum.FIND_TRANSACTION_GROUP.getCode());
        transactionRequest.setRequestId(request.getRequestId());
        transactionRequest.setResult(ResultEnum.SUCCESS.getCode());
        TransactionGroup group = new TransactionGroup();
        List<TransactionItem> transactionItemList = managerService.selectByTransactionGroupId(transactionGroup.getTransactionId());
        group.setTransactionItemList(transactionItemList);
        group.setTransactionId(transactionGroup.getTransactionId());
        transactionRequest.setTransactionGroup(transactionGroup);
        ctx.writeAndFlush(request);
    }

    /**
     * 获取事务组状态
     * @param ctx
     * @param request
     * @param transactionGroup
     */
    private void executeGetTransactionStatus(ChannelHandlerContext ctx, TransactionRequest request, TransactionGroup transactionGroup) {
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionGroup group = new TransactionGroup();
        String status = managerService.selectTransactionGroupStatus(transactionGroup.getTransactionId());
        group.setStatus(status);
        group.setTransactionId(transactionGroup.getTransactionId());
        transactionRequest.setTransactionGroup(transactionGroup);
        transactionRequest.setResult(ResultEnum.SUCCESS.getCode());
        transactionRequest.setRequestId(request.getRequestId());
        transactionRequest.setAction(ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode());
        ctx.writeAndFlush(transactionRequest);
    }

    /**
     * 添加事务
     * @param ctx
     * @param request
     * @param transactionGroup
     */
    private void executeAddTransaction(ChannelHandlerContext ctx, TransactionRequest request, TransactionGroup transactionGroup) {
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionGroup group = new TransactionGroup();
        group.setTransactionId(IdUtil.getTransactionGroupId());
        List<TransactionItem> transactionItemList = transactionGroup.getTransactionItemList();
        if(CollectionUtils.isNotEmpty(transactionItemList)){
            TransactionItem transactionItem = transactionItemList.get(0);
            transactionItem.setRemoteAddr(RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            group.getTransactionItemList().add(transactionItem);
            managerService.addTransaction(transactionGroup.getTransactionId(),transactionItem);
        }
        transactionRequest.setAction(ActionEnum.RECEIVE.getCode());
        transactionRequest.setRequestId(request.getRequestId());
        transactionRequest.setResult(ResultEnum.SUCCESS.getCode());
        ctx.writeAndFlush(transactionRequest);
    }

    /**
     * 创建事务组
     * @param ctx
     * @param request
     */
    private void executeCreateTransactionGroup(ChannelHandlerContext ctx, TransactionRequest request) {
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setTransactionId(IdUtil.getTransactionGroupId());
        List<TransactionItem> transactionItemList = new ArrayList<>();
        transactionGroup.setTransactionItemList(transactionItemList);
        managerService.saveTransactionGroup(transactionGroup);
        transactionRequest.setTransactionGroup(transactionGroup);
        transactionRequest.setResult(ResultEnum.SUCCESS.getCode());
        transactionRequest.setAction(ActionEnum.RECEIVE.getCode());
        ctx.writeAndFlush(transactionRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty server出现异常");
        cause.printStackTrace();
        ctx.close();
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
            log.info("netty连接达到了最大连接数，连接关闭");
            ctx.close();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        NettyManager.getInstance().removeChannel(ctx.channel());
    }
}
