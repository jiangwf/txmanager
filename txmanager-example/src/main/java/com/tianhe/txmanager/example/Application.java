package com.tianhe.txmanager.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: he.tian
 * @time: 2018-12-18 16:22
 */
@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
        log.info("txManager client startup");
    }
}
