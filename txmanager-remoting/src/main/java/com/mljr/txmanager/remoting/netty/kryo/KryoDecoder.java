package com.mljr.txmanager.remoting.netty.kryo;

import com.mljr.txmanager.remoting.netty.AbstractMessageDecoder;
import com.mljr.txmanager.remoting.netty.Codec;

/**
 * @author: he.tian
 * @time: 2018-10-16 17:58
 */
public class KryoDecoder extends AbstractMessageDecoder{

    public KryoDecoder(Codec codec) {
        super(codec);
    }
}
