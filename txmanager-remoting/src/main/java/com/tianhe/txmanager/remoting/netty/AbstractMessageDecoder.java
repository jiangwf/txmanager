package com.tianhe.txmanager.remoting.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: he.tian
 * @time: 2018-10-16 18:00
 */
public class AbstractMessageDecoder extends ByteToMessageDecoder{

    private static final int MSG_LENGTH = MessageCoder.MSG_LENGTH;

    private MessageCoder messageCoder;

    public AbstractMessageDecoder(final MessageCoder messageCoder){
        this.messageCoder = messageCoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < AbstractMessageDecoder.MSG_LENGTH){
            return;
        }
        in.markReaderIndex();
        int msgLength = in.readInt();
        if(msgLength < 0){
            ctx.close();
        }
        if(in.readableBytes() < msgLength){
            in.resetReaderIndex();
        }else{
            byte[] messageBody = new byte[msgLength];
            in.readBytes(messageBody);
            try {
                Object obj = messageCoder.decode(messageBody);
                out.add(obj);
            }catch (IOException ex){
                Logger.getLogger(AbstractMessageDecoder.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }
}
