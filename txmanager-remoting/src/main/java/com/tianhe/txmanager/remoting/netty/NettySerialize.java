package com.tianhe.txmanager.remoting.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: he.tian
 * @time: 2018-10-16 11:33
 */
public interface NettySerialize {

    void serialize(OutputStream output,Object object) throws IOException;

    Object deserialize(InputStream input) throws IOException;
}
