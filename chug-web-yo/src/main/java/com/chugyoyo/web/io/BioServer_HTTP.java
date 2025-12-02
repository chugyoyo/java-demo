package com.chugyoyo.web.io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer_HTTP {
    private static final int PORT = 8080;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(500);

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("BIO HTTP Server 启动，监听端口: " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.execute(() -> handleHttp(clientSocket));
        }
    }

    private static void handleHttp(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream())
        ) {

            // 1. 读取 HTTP 请求行，例如：GET / HTTP/1.1
            String requestLine = in.readLine();
            if (requestLine == null) return;

            System.out.println("收到 HTTP 请求: " + requestLine);

            // 2. 跳过剩下的 HTTP Header
            String header;
            while ((header = in.readLine()) != null && header.length() != 0) {
                // 读取 header，但不处理（简单 demo）
            }

            // 3. 准备响应内容
            String body = "<html><body><h1>Hello from BIO HTTP Server</h1></body></html>";

            // 4. 写入 HTTP 响应
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println("Content-Length: " + body.getBytes().length);
            out.println("Connection: close");
            out.println();       // 空行分隔 header 与 body
            out.println(body);   // 响应体
            out.flush();

        } catch (Exception e) {
            System.err.println("BIO HTTP 客户端异常: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }
}

