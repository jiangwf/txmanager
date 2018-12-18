package com.tianhe.txmanager.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:53
 */
public class AbstractMessageEncoder extends MessageToByteEncoder<Object>{

    private MessageCoder messageCoder;

    public AbstractMessageEncoder(final MessageCoder messageCoder){
        this.messageCoder = messageCoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        messageCoder.encode(out,msg);
    }
}
