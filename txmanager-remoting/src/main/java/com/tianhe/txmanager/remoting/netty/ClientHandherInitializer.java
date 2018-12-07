package com.tianhe.txmanager.remoting.netty;

import com.tianhe.txmanager.remoting.codec.kryo.KryoDecoder;
import com.tianhe.txmanager.remoting.codec.kryo.KryoEncoder;
import com.tianhe.txmanager.remoting.codec.kryo.KryoMessageCoder;
import com.tianhe.txmanager.remoting.codec.kryo.KryoPoolFactory;
import com.tianhe.txmanager.remoting.config.ClientConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @author: he.tian
 * @time: 2018-11-01 17:25
 */
@Component
public class ClientHandherInitializer extends ChannelInitializer<SocketChannel>{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private NettyClientHandler nettyClientHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        KryoMessageCoder messageCoder = new KryoMessageCoder(KryoPoolFactory.getKryoPoolInstance());
        pipeline.addLast(new KryoEncoder(messageCoder));
        pipeline.addLast(new KryoDecoder(messageCoder));
        pipeline.addLast("timeout",new IdleStateHandler(clientConfig.getHeartTime(),clientConfig.getHeartTime(),clientConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyClientHandler);
    }
}
