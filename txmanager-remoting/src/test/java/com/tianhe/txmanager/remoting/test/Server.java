package com.tianhe.txmanager.remoting.test;

import com.tianhe.txmanager.remoting.config.ServerConfig;
import com.tianhe.txmanager.remoting.netty.NettyServer;
import com.tianhe.txmanager.remoting.netty.ServerChannelInitializer;

/**
 * @author: he.tian
 * @time: 2018-12-18 15:28
 */
public class Server {

    public static void main(String[] args) {
        NettyServer server = new NettyServer();

        ServerConfig config = new ServerConfig();
        config.setDelayTime(30);
        config.setHeartTime(30);
        config.setMaxConnection(100);
        config.setPort(9056);
        config.setThreads(Runtime.getRuntime().availableProcessors());
        server.setServerConfig(config);

        ServerChannelInitializer serverChannelInitializer = new ServerChannelInitializer();
        server.setServerChannelInitializer(serverChannelInitializer);

        server.start();

    }
}
