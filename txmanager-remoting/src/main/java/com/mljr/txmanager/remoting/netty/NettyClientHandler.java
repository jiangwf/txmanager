package com.mljr.txmanager.remoting.netty;

import com.mljr.txmanager.core.SpringUtil;
import com.mljr.txmanager.core.model.NettyActionEnum;
import com.mljr.txmanager.core.model.NettyRequest;
import com.mljr.txmanager.remoting.config.ClientConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:44
 */
@Component
public class NettyClientHandler extends ChannelInboundHandlerAdapter{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    private ClientConfig clientConfig;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //TODO
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                SpringUtil.INSTANCE.getBean(NettyClient.class).doConnect();
            }else if (event.state() == IdleState.WRITER_IDLE){
                NettyRequest request = new NettyRequest();
                request.setAction(NettyActionEnum.HEART.getCode());
                ctx.writeAndFlush(request);
                logger.info("发送事务管理器心跳检测");
            }else if(event.state() == IdleState.ALL_IDLE){
                SpringUtil.INSTANCE.getBean(NettyClient.class).doConnect();
            }
        }
    }
}
