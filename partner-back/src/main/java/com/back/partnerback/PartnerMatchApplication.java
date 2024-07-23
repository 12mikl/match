package com.back.partnerback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@MapperScan("com.back.partnerback.mapper")
public class PartnerMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerMatchApplication.class, args);
    }

}
