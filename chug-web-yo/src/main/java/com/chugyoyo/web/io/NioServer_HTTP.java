package com.chugyoyo.web.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer_HTTP {
    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(PORT));

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO HTTP Server 启动 (epoll)，监听端口: " + PORT);

        while (true) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    handleAccept(selector, serverChannel);
                } else if (key.isReadable()) {
                    handleHttpRead(key);
                }
            }
        }
    }

    private static void handleAccept(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void handleHttpRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(2048);
        StringBuilder request = new StringBuilder();

        // 读取 HTTP 全部请求头（非阻塞读）
        while (true) {
            buffer.clear();
            int read = channel.read(buffer);

            if (read > 0) {
                buffer.flip();
                request.append(new String(buffer.array(), 0, read));

                // HTTP Header 结束符：\r\n\r\n
                if (request.indexOf("\r\n\r\n") != -1) {
                    break;
                }

            } else if (read == 0) {
                break;
            } else {
                channel.close();
                key.cancel();
                return;
            }
        }

        if (request.length() > 0) {
//            String[] firstLine = request.toString().split("\r\n");
            System.out.println("收到 HTTP 请求: " + request);
        }

        // 构造响应体
        String body = "<html><body><h1>Hello from NIO HTTP Server</h1></body></html>";

        // 构造 HTTP 响应
        String response =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + body.getBytes().length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        body;

        ByteBuffer respBuffer = ByteBuffer.wrap(response.getBytes());
        channel.write(respBuffer);

        channel.close();
        key.cancel();
    }
}
