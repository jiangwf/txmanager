package com.tianhe.txmanager.core.codec.kryo;

import com.tianhe.txmanager.core.codec.AbstractMessageEncoder;
import com.tianhe.txmanager.core.codec.MessageCoder;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:52
 */
public class KryoEncoder extends AbstractMessageEncoder {

    public KryoEncoder(final MessageCoder messageCoder){
        super(messageCoder);
    }
}
