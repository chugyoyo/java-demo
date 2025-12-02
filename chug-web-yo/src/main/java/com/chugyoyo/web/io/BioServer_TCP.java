package com.chugyoyo.web.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer_TCP {
    private static final int PORT = 8080;

    // 使用线程池管理连接，每个连接独占一个线程
    private static ExecutorService executorService = Executors.newFixedThreadPool(500);

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("BIO TCP Echo Server 启动，监听端口: " + PORT);

        while (true) {
            // 1. 阻塞点：主线程阻塞，等待客户端连接
            Socket clientSocket = serverSocket.accept();

            // 2. 为每个连接分配一个新线程进行处理
            executorService.execute(() -> handleConnection(clientSocket));
        }
    }

    private static void handleConnection(Socket clientSocket) {
        // 使用 try-with-resources 确保资源自动关闭
        try (
                // 字节流转换为字符流，并使用 BufferedReader 实现按行读取
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // PrintWriter 自动添加换行符，并自动 flush
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String inputLine;
            // 3. 阻塞点：工作线程阻塞，等待换行符 (\n) 出现
            // TCP Sampler 发送 'Hello\n' 后，readLine() 才能返回
            if ((inputLine = in.readLine()) != null) {
                System.out.println("收到 BIO 消息: " + inputLine);
                // 4. 回复客户端，println 会自动加上 \n，TCP Sampler 识别为消息结束
                out.println("BIO Echo: " + inputLine);
            }
        } catch (Exception e) {
            // 忽略连接异常（例如客户端强制关闭）
            System.err.println("BIO 客户端连接异常: " + e.getMessage());
        }
        // 5. 连接在 try-with-resources 结束后自动关闭 (短连接测试的关键)
    }
}
