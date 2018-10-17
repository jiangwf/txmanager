package com.mljr.txmanager.remoting.netty;

import com.sun.javafx.PlatformUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.annotation.PostConstruct;

/**
 * @author: he.tian
 * @time: 2018-10-15 14:46
 */
public class NettyServer{

    @PostConstruct
    public void start(){
        EventLoopGroup bossGroup = PlatformUtil.isLinux() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup workerGroup = new PlatformUtil().isLinux() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(PlatformUtil.isLinux() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ser)
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
