package com.mljr.txmanager.remoting.netty;

import com.mljr.txmanager.common.Task;
import com.mljr.txmanager.common.enums.ActionEnum;
import com.mljr.txmanager.common.enums.ResultEnum;
import com.mljr.txmanager.common.model.TransactionGroup;
import com.mljr.txmanager.common.model.TransactionItem;
import com.mljr.txmanager.common.model.TransactionRequest;
import com.mljr.txmanager.core.ManagerContext;
import com.mljr.txmanager.core.SpringUtil;
import com.mljr.txmanager.remoting.config.ClientConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:44
 */
@Component
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    private ClientConfig clientConfig;

    @Autowired
    private ManagerContext managerContext;

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
        Task task = managerContext.getTask(request.getTaskId());
        task.setResult(request.getTransactionGroup());
        task.singal();
    }

    /**
     * 获取事务组状态
     * @param request
     */
    private void getTransactionGroupStatus(TransactionRequest request) {
        Task task = managerContext.getTask(request.getTaskId());
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
            Task task = managerContext.getTask(transactionItem.getTaskId());
            task.setResult(transactionItem.getStatus());
            task.singal();
        }
    }

    /**
     * 接受请求
     * @param request
     */
    private void receive(TransactionRequest request) {
        Task task = managerContext.getTask(request.getTaskId());
        task.setResult(ResultEnum.SUCCESS.getCode().equals(request.getResult()));
        task.singal();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                SpringUtil.INSTANCE.getBean(NettyClient.class).doConnect();
            }else if (event.state() == IdleState.WRITER_IDLE){
                TransactionRequest transactionRequest = new TransactionRequest();
                transactionRequest.setAction(ActionEnum.HEART_BEAT.getCode());
                ctx.writeAndFlush(transactionRequest);
                logger.info("txManager 发送事务管理器心跳检测");
            }else if(event.state() == IdleState.ALL_IDLE){
                SpringUtil.INSTANCE.getBean(NettyClient.class).doConnect();
            }
        }
    }
}
