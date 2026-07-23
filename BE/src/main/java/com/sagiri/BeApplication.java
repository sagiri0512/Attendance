package com.sagiri;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * HR考勤工资系统 - Spring Boot 启动类
 *
 * <p>扫描 com.sagiri.mapper 包下的所有 MyBatis Mapper 接口。</p>
 *
 * @author sagiri
 */
@EnableScheduling
@SpringBootApplication
@MapperScan("com.sagiri.mapper")
public class BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeApplication.class, args);
    }

}
