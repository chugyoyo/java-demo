package com.chugyoyo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class StartChugWebYoProvider {

    public static void main(String[] args) {
        SpringApplication.run(StartChugWebYoProvider.class, args);
    }
}
