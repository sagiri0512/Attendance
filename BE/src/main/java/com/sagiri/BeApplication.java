package com.sagiri;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sagiri.mapper")
public class BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeApplication.class, args);
    }

}
