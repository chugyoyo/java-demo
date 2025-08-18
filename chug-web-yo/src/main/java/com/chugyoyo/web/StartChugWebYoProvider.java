package com.chugyoyo.web;

import com.chugyoyo.web.nio.ZeroCopyNettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class StartChugWebYoProvider {

    @Value("${netty.server.port}")
    private int nettyPort;

    @PostConstruct
    public void init() {
        try {
            new ZeroCopyNettyServer(nettyPort).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(StartChugWebYoProvider.class, args);
    }
}
