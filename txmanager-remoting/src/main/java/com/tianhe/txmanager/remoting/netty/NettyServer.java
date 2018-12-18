package com.tianhe.txmanager.remoting.netty;

import com.sun.javafx.PlatformUtil;
import com.tianhe.txmanager.common.exception.TransactionException;
import com.tianhe.txmanager.remoting.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
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
import java.net.InetAddress;

/**
 * @author: he.tian
 * @time: 2018-10-15 14:46
 */
@Service
public class NettyServer{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private ServerChannelInitializer serverChannelInitializer;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @PostConstruct
    public void start(){
        logger.info("txManager netty server启动");
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
            logger.info("====================================================");
            logger.info("netty server启动，ip地址={},端口号={}", InetAddress.getLocalHost().getHostAddress(),serverConfig.getPort());
            logger.info("====================================================");
            ChannelFuture channelFuture = serverBootstrap.bind(serverConfig.getPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("txManager netty server启动失败",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void stop(){
        logger.info("txManager netty server停止");
        try {
            if(bossGroup != null){
                bossGroup.shutdownGracefully().await();
            }
            if(workerGroup != null){
                workerGroup.shutdownGracefully().await();
            }
        } catch (InterruptedException e) {
            logger.error("txManager netty server停止失败，异常信息={}",e);
            throw new TransactionException("txManager netty server停止失败");
        }
    }
}
