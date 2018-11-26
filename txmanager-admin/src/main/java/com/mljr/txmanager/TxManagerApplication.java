package com.mljr.txmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: he.tian
 * @time: 2018-11-22 14:07
 */
@SpringBootApplication
@Slf4j
public class TxManagerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TxManagerApplication.class);
        application.run(args);
        log.info("txManager Server startup");
    }
}
