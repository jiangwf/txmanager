package com.tianhe.txmanager.admin.test;

import com.tianhe.txmanager.server.config.ServerConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: he.tian
 * @time: 2018-12-19 10:53
 */
public class ServerConfigTest extends AppTest {

    @Autowired
    private ServerConfig serverConfig;

    @Test
    public void testServerConfig(){
        System.out.println(serverConfig);
    }
}
