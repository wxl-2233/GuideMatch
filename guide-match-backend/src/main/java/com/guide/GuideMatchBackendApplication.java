package com.guide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@MapperScan("com.guide.mapper")
public class GuideMatchBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuideMatchBackendApplication.class, args);
    }
}
