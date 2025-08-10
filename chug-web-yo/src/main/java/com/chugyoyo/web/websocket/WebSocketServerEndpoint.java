package com.chugyoyo.web.websocket;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket-endpoint")
public class WebSocketServerEndpoint {

    @Getter
    private static final Set<WebSocketServerEndpoint> connections = new CopyOnWriteArraySet<>();

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        connections.add(this);
        System.out.println("新连接: " + session.getId());
        sendMessage("欢迎连接到 WebSocket 服务！");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("收到消息: " + message);
        // 广播消息
        for (WebSocketServerEndpoint client : connections) {
            client.sendMessage("客户端[" + session.getId() + "]说: " + message);
        }
    }

    @OnClose
    public void onClose() {
        connections.remove(this);
        System.out.println("连接关闭: " + session.getId());
    }

    @OnError
    public void onError(Throwable error) {
        System.err.println("错误: " + error.getMessage());
    }

    private void sendMessage(String msg) {
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
