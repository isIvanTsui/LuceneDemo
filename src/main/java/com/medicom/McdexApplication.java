package com.medicom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.medicom.**.mapper.**")
public class McdexApplication {

    public static void main(String[] args) {
        SpringApplication.run(McdexApplication.class, args);
    }

}
