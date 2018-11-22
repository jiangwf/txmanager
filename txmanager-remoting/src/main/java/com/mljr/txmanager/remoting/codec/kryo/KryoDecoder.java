package com.mljr.txmanager.remoting.codec.kryo;

import com.mljr.txmanager.remoting.netty.AbstractMessageDecoder;
import com.mljr.txmanager.remoting.netty.MessageCoder;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:58
 */
public class KryoDecoder extends AbstractMessageDecoder{

    public KryoDecoder(MessageCoder messageCoder) {
        super(messageCoder);
    }
}
