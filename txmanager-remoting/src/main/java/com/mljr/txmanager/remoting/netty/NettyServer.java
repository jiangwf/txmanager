package com.mljr.txmanager.remoting.netty;

import com.mljr.txmanager.remoting.TransactionException;
import com.mljr.txmanager.remoting.config.ServerConfig;
import com.sun.javafx.PlatformUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author: he.tian
 * @time: 2018-10-15 14:46
 */
@Service
public class NettyServer{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ServerConfig serverConfig;

    private ServerChannelInitializer serverChannelInitializer;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @Autowired
    public NettyServer(ServerConfig serverConfig, ServerChannelInitializer serverChannelInitializer){
        this.serverConfig = serverConfig;
        this.serverChannelInitializer = serverChannelInitializer;
    }

    @PostConstruct
    public void start(){
         bossGroup = PlatformUtil.isLinux() ? new EpollEventLoopGroup(serverConfig.getThreads()) : new NioEventLoopGroup(serverConfig.getThreads());
         workerGroup = PlatformUtil.isLinux() ? new EpollEventLoopGroup(serverConfig.getThreads()) : new NioEventLoopGroup(serverConfig.getThreads());
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(PlatformUtil.isLinux() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,100)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(serverChannelInitializer);
        } catch (Exception e) {
            logger.error("netty server初始化失败",e);
        } finally {
        }
    }

    @PreDestroy
    public void stop(){
        try {
            if(bossGroup != null){
                bossGroup.shutdownGracefully().await();
            }
            if(workerGroup != null){
                workerGroup.shutdownGracefully().await();
            }
        } catch (InterruptedException e) {
            logger.error("netty服务停止失败={}",e);
            throw new TransactionException("netty服务停止失败");
        }
    }
}
