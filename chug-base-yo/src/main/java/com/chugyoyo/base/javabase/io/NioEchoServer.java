package com.chugyoyo.base.javabase.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NioEchoServer {

    public static void main(String[] args) throws IOException {
        // 1️⃣ 创建 Selector
        Selector selector = Selector.open();

        // 2️⃣ 打开 ServerSocketChannel 并绑定端口
        // 可以直接使用 telnet 或 nc 命令，`telnet localhost 8090`
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8090));
        serverChannel.configureBlocking(false);

        // 3️⃣ 注册“接收连接”事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("✅ NIO Echo Server started on port 8080 ...");

        // 4️⃣ 主循环
        while (true) {
            // 阻塞直到有事件发生
            selector.select();

            // 获取所有就绪的事件集合
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove(); // 一定要移除，否则会重复处理

                if (key.isAcceptable()) {
                    // 5️⃣ 有新连接到达
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    System.out.println("🟢 Connected: " + client.getRemoteAddress());

                    // 注册“可读事件”
                    client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                } else if (key.isReadable()) {
                    // 6️⃣ 有数据可读
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();

                    int bytesRead = client.read(buffer);
                    if (bytesRead == -1) {
                        System.out.println("🔴 Client disconnected");
                        client.close();
                    } else if (bytesRead > 0) {
                        buffer.flip(); // 从写模式切换到读模式
                        client.write(buffer); // 回显数据
                        buffer.clear(); // 清空 buffer，为下次读做准备
                    }
                }
            }
        }
    }
}
