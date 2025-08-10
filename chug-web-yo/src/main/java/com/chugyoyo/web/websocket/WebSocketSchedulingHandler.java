package com.chugyoyo.web.websocket;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableScheduling
public class WebSocketSchedulingHandler {

    @Scheduled(cron = "0/3 * * * * ? ")
    public void nowOnline() {
        Set<WebSocketServerEndpoint> webSocketSet = WebSocketServerEndpoint.connections;
        webSocketSet.forEach(c -> {
            c.sendMessage("目前在线人数" + webSocketSet.size());
        });
    }
}
