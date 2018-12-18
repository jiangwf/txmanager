package com.tianhe.txmanager.core.codec.kryo;


import com.tianhe.txmanager.core.codec.AbstractMessageDecoder;
import com.tianhe.txmanager.core.codec.MessageCoder;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:58
 */
public class KryoDecoder extends AbstractMessageDecoder {

    public KryoDecoder(MessageCoder messageCoder) {
        super(messageCoder);
    }
}
