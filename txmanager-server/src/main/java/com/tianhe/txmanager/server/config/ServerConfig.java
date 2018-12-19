package com.tianhe.txmanager.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author: he.tian
 * @time: 2018-10-31 17:42
 */
@Data
@Component
@PropertySource("classpath:server-config.properties")
public class ServerConfig {

    /**
     * 服务启动端口
     */
    @Value("${port}")
    private int port;

    /**
     * 最大线程数
     */
    private int threads = Runtime.getRuntime().availableProcessors();

    /**
     * 客户端和服务端的最大连接数
     */
    @Value("${maxConnection}")
    private int maxConnection = 100;

    /**
     * 与客户端通信最大延迟时间，超过该时间返回失败
     */
    @Value("${delayTime}")
    private int delayTime;

    /**
     * 与客户端保持心跳的时间
     */
    @Value("${heartTime}")
    private int heartTime;
}
