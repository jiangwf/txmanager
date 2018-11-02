package com.mljr.txmanager.remoting.netty;

import com.mljr.txmanager.remoting.config.ClientConfig;
import com.sun.javafx.PlatformUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-11-01 17:23
 */
@Component
public class NettyClient{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private EventLoopGroup workerGroup;

    private ClientHandherInitializer clientHandherInitializer;

    private ClientConfig clientConfig;

    private Channel channel;

    private Bootstrap bootstrap;

    @Autowired
    public NettyClient(ClientHandherInitializer clientHandherInitializer,ClientConfig clientConfig){
        this.clientHandherInitializer = clientHandherInitializer;
        this.clientConfig = clientConfig;
    }

    @PostConstruct
    public void start(){
        try {
            workerGroup = PlatformUtil.isLinux() ? new EpollEventLoopGroup(clientConfig.getThreads()) : new NioEventLoopGroup(clientConfig.getThreads());
            bootstrap = new Bootstrap();
            bootstrap.channel(PlatformUtil.isLinux() ? EpollSocketChannel.class : NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG,1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(clientHandherInitializer);
            doConnect();
        } catch (Exception e) {
            logger.error("netty client初始化失败",e);
        }
    }

    @PreDestroy
    public void stop() throws Exception {
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }

    private void doConnect(){
        if(channel != null && channel.isActive()){
            return;
        }
        ChannelFuture future = bootstrap.connect(clientConfig.getTxManagerHost(), clientConfig.getTxManagerPort());
        logger.info("netty client连接netty server=======url={}", clientConfig.getTxManagerHost()+clientConfig.getTxManagerPort());
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    channel = channelFuture.channel();
                    logger.info("netty client连接netty server成功，url={}",clientConfig.getTxManagerHost()+clientConfig.getTxManagerPort());
                }else{
                    logger.info("netty client连接netty server失败，5s后重试,url={}",clientConfig.getTxManagerHost()+clientConfig.getTxManagerPort());
                    channelFuture.channel().eventLoop().schedule(NettyClient.this::doConnect,5, TimeUnit.SECONDS);
                }
            }
        });
    }
}
