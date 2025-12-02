package com.chugyoyo.web.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioServer_TCP {
    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO TCP Echo Server 启动 (epoll)，监听端口: " + PORT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                try {
                    if (key.isAcceptable()) {
                        handleAccept(selector, serverChannel);
                    } else if (key.isReadable()) {
                        // 1. 调用新的 TCP 读写方法
                        handleTcpRead(key);
                    }
                } catch (Exception e) {
                    System.err.println("处理连接时发生异常：" + e.getMessage());
                    // 发生异常时，关闭连接
                    key.cancel();
                    if (key.channel() != null) {
                        key.channel().close();
                    }
                }
            }
        }
    }

    private static void handleAccept(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        if (clientChannel == null) {
            return;
        }
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 处理纯 TCP 请求：按行读取，回复 Echo，然后关闭连接（短连接模式）
     */
    private static void handleTcpRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        // 读缓冲区，足够容纳一行 TCP Sampler 发送的数据
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder request = new StringBuilder();

        // ---- 读取阶段：确保读取到足够的数据（至少是一个完整的行） ----
        // 循环读取数据，直到找到换行符（\n）或者没有更多数据可读
        while (true) {
            int read = channel.read(buffer); // 非阻塞读

            if (read > 0) {
                buffer.flip();
                request.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
            } else if (read == 0) {
                // 当前没有数据可读，退出循环，等待下一次事件
                break;
            } else {
                // read < 0：对端已关闭连接，清理资源
                channel.close();
                key.cancel();
                return;
            }
        }

        // ---- 业务处理：Echo 回复 ----
        String receivedMessage = request.toString().trim();
        if (!receivedMessage.isEmpty()) {
            System.out.println("收到 TCP 消息: " + receivedMessage);

            // 3. 构造 Echo 响应，必须包含换行符 (\n)
            String responseText = "NIO Echo: " + receivedMessage + "\n";
            byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);

            ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);

            // 4. 写回数据：确保所有数据写出
            while (responseBuffer.hasRemaining()) {
                channel.write(responseBuffer);
            }
        }

        // ---- 清理连接（短连接模式） ----
        // 5. 确保发送 FIN 包，然后关闭 Socket
        try {
            // 半关闭输出流，发送 FIN
            channel.shutdownOutput();
        } catch (IOException e) {
            // 忽略某些异常
        }

        // 6. 最后关闭 channel 并取消 key
        channel.close();
        key.cancel();
    }
}