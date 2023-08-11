package com.baijiacms.qsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 11:07
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
