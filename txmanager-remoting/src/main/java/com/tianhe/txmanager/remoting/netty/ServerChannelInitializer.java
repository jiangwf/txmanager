package com.tianhe.txmanager.remoting.netty;

import com.tianhe.txmanager.remoting.codec.kryo.KryoDecoder;
import com.tianhe.txmanager.remoting.codec.kryo.KryoEncoder;
import com.tianhe.txmanager.remoting.codec.kryo.KryoMessageCoder;
import com.tianhe.txmanager.remoting.codec.kryo.KryoPoolFactory;
import com.tianhe.txmanager.remoting.config.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: he.tian
 * @time: 2018-10-16 11:11
 */
@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        KryoMessageCoder kryoMessageCoder = new KryoMessageCoder(KryoPoolFactory.getKryoPoolInstance());
        pipeline.addLast(new KryoEncoder(kryoMessageCoder));
        pipeline.addLast(new KryoDecoder(kryoMessageCoder));
        pipeline.addLast("timeout",new IdleStateHandler(serverConfig.getHeartTime(),serverConfig.getHeartTime(),serverConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyServerHandler);
    }
}
