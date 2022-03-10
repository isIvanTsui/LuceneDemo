package com.ivan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * lucene演示应用程序
 *
 * @author cuiyingfan
 * @date 2022/03/10
 */
@SpringBootApplication
@MapperScan("com.ivan.**.mapper.**")
public class LuceneDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuceneDemoApplication.class, args);
    }

}
