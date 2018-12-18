package com.tianhe.txmanager.core.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * 消息编码服务
 * @author: he.tian
 * @time: 2018-10-16 11:20
 */
public interface MessageCoder {

    /**
     * 消息定长
     */
    int MSG_LENGTH = 4;

    /**
     * 对消息进行编码
     * @param out
     * @param msg
     * @throws IOException
     */
    void encode(ByteBuf out, Object msg) throws IOException;

    /**
     * 对消息进行解码
     * @param body
     * @throws IOException
     */
    Object decode(byte[] body) throws IOException;
}
