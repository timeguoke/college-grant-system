package com.collegegrant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.collegegrant.mapper")
public class CollegeGrantSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollegeGrantSystemApplication.class, args);
    }

}
