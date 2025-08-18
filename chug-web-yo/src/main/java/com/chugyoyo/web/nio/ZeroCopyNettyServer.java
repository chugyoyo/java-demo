package com.chugyoyo.web.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

@Slf4j
public class ZeroCopyNettyServer {

    private final int port;

    public ZeroCopyNettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new FileServerHandler());
                        }
                    });

            Channel ch = b.bind(port).sync().channel();
            log.info("Netty server started at port:{} (http)", port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private static final String FILE_PATH = "/tmp/testfile.bin";

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
            if ("/zerocopy".equals(req.uri())) {
                File file = new File(FILE_PATH);
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();

                HttpResponse response = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.OK);
                HttpUtil.setContentLength(response, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                ctx.write(response);

                // 真正的零拷贝（sendfile）
                ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength));

                ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}