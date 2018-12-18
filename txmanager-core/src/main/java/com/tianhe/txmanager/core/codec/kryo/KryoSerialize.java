package com.tianhe.txmanager.core.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.tianhe.txmanager.core.codec.NettySerialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: he.tian
 * @time: 2018-10-16 11:34
 */
public class KryoSerialize implements NettySerialize {

    private KryoPool pool;

    public KryoSerialize(final KryoPool pool){
        this.pool = pool;
    }

    @Override
    public void serialize(OutputStream output, Object object) throws IOException {
        Kryo kryo = pool.borrow();
        Output out = new Output(output);
        kryo.writeClassAndObject(out,object);
        out.close();
        output.close();
        pool.release(kryo);
    }

    @Override
    public Object deserialize(InputStream input) throws IOException {
        Kryo kryo = pool.borrow();
        try {
            Input in = new Input(input);
            return kryo.readClassAndObject(in);
        } finally {
            input.close();
            pool.release(kryo);
        }
    }
}
