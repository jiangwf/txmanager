package com.tianhe.txmanager.core.codec.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.tianhe.txmanager.common.model.TransactionRequest;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @author: he.tian
 * @time: 2018-10-16 15:23
 */
public final class KryoPoolFactory {

    private static volatile KryoPoolFactory poolFactory;

    private KryoFactory factory = () ->{
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(TransactionRequest.class);
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return kryo;
    };

    private KryoPool pool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory(){

    }

    public static KryoPool getKryoPoolInstance(){
        if(poolFactory == null){
            synchronized (KryoPoolFactory.class){
                if(poolFactory == null){
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }

    public KryoPool getPool() {
        return pool;
    }
}
