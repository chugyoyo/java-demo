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
        // 1. 创建 Selector (核心，映射到底层 epoll)
        Selector selector = Selector.open();

        // 2. 开启 ServerSocketChannel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false); // 必须设置为非阻塞
        serverChannel.socket().bind(new InetSocketAddress(PORT));

        // 3. 注册接收事件到 Selector
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO Server 启动 (epoll)，监听端口: " + PORT);

        while (true) {
            // 4. 阻塞点：单线程阻塞，等待就绪事件发生
            selector.select();

            // 获取所有就绪事件
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove(); // 移除当前事件，防止重复处理

                if (key.isAcceptable()) {
                    // 处理连接事件
                    handleAccept(selector, serverChannel);
                } else if (key.isReadable()) {
                    // 处理读取事件
                    handleRead(key);
                }
            }
        }
    }

    private static void handleAccept(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept(); // 非阻塞
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    // 优化后的 handleRead (按行读取)
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();

        // 循环读取数据，直到读完当前所有可用数据
        while (true) {
            buffer.clear();
            int readBytes = clientChannel.read(buffer);

            if (readBytes > 0) {
                buffer.flip();
                sb.append(new String(buffer.array(), 0, readBytes));

                // 找到消息边界（例如换行符 \n）
                if (sb.toString().contains("\n")) {
                    // 找到边界，跳出读取循环
                    break;
                }
            } else if (readBytes == 0) {
                // 当前没有更多数据可用 (非阻塞模式的特点)
                break;
            } else if (readBytes < 0) {
                // 客户端断开连接
                clientChannel.close();
                return;
            }
        }

        String received = sb.toString().trim();
        if (!received.isEmpty()) {
            System.out.println("收到 NIO 消息: " + received);

            // 确保响应后关闭连接
            ByteBuffer response = ByteBuffer.wrap(("NIO Echo: " + received + "\n").getBytes());
            clientChannel.write(response);
        }

        // 无论是否读到数据，都关闭连接以结束本次短连接的事务
        clientChannel.close();
        key.cancel();
    }
}