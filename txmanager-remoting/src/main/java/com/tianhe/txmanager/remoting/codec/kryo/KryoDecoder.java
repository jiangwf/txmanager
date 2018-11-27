package com.tianhe.txmanager.remoting.codec.kryo;

import com.tianhe.txmanager.remoting.netty.AbstractMessageDecoder;
import com.tianhe.txmanager.remoting.netty.MessageCoder;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:58
 */
public class KryoDecoder extends AbstractMessageDecoder{

    public KryoDecoder(MessageCoder messageCoder) {
        super(messageCoder);
    }
}
