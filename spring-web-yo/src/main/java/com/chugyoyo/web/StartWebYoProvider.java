package com.chugyoyo.web;

import com.chugyoyo.web.annotation.source.SpringCodeCheck;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringCodeCheck
@SpringBootApplication
public class StartWebYoProvider {

    public static void main(String[] args) {
        SpringApplication.run(StartWebYoProvider.class, args);
    }
}
