package com.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 知识点:@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 *
 * @author youben
 */
@SpringBootApplication(scanBasePackages = "com.notification")        //核心注解
@MapperScan("com.notification.dao")                //扫描 mybatis mapper 包路径
public class BootNotificationApplication {

    public static void main(String[] args) {

        SpringApplication.run(BootNotificationApplication.class, args);
    }
}
