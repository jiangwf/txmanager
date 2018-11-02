package com.mljr.txmanager.remoting.netty.kryo;

import com.mljr.txmanager.remoting.netty.AbstractMessageEncoder;
import com.mljr.txmanager.remoting.netty.MessageCoder;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:52
 */
public class KryoEncoder extends AbstractMessageEncoder {

    public KryoEncoder(final MessageCoder messageCoder){
        super(messageCoder);
    }
}
