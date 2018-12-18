package com.tianhe.txmanager.core.codec.kryo;

import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;
import com.tianhe.txmanager.core.codec.MessageCoder;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kyro编解码实现
 * @author: he.tian
 * @time: 2018-10-16 11:23
 */
public class KryoMessageCoder implements MessageCoder {

    private KryoPool pool;

    public KryoMessageCoder(final KryoPool pool){
        this.pool = pool;
    }

    @Override
    public void encode(ByteBuf out, Object msg) throws IOException {
        Closer closer = Closer.create();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            KryoSerialize kryoSerialize = new KryoSerialize(pool);
            kryoSerialize.serialize(byteArrayOutputStream,msg);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        }finally {
            closer.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        Closer closer = Closer.create();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            KryoSerialize kryoSerialize = new KryoSerialize(pool);
            return kryoSerialize.deserialize(byteArrayInputStream);
        } finally {
            closer.close();
        }
    }
}
