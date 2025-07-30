package com.chugyoyo.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.chugyoyo.db.mapper")
@SpringBootApplication
public class StartChugDbYoProvider {

    public static void main(String[] args) {
        SpringApplication.run(StartChugDbYoProvider.class, args);
    }
}
