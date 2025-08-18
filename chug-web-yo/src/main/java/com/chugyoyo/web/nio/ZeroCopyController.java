package com.chugyoyo.web.nio;

import com.chugyoyo.web.distribute.monitor.Monitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * 测试零拷贝
 */
@RequestMapping("/zero-copy")
@RestController
public class ZeroCopyController {

    private static final String FILE_PATH = "/tmp/testfile.bin";

    /**
     * 普通 I/O 下载（带多次拷贝）
     */
    @Monitor
    @GetMapping("/download/normal")
    public void normalDownload(HttpServletResponse response) throws IOException {
        File file = new File(FILE_PATH);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"normal.bin\"");
        response.setContentLengthLong(file.length());

        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len); // 用户态 -> 内核态
            }
            out.flush();
        }
    }

    /**
     * 零拷贝下载（FileChannel.transferTo -> sendfile）
     */
    @Monitor
    @GetMapping("/download/zerocopy")
    public void zeroCopyDownload(HttpServletResponse response) throws IOException {
        File file = new File(FILE_PATH);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"zerocopy.bin\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file);
             FileChannel channel = fis.getChannel();
             OutputStream out = response.getOutputStream()) {

            // Servlet OutputStream 转换成 FileChannel
            // 核心是 getChannel()，但 ServletOutputStream 没有
            // 解决方案：用 WritableByteChannel 封装
            WritableByteChannel writableChannel = Channels.newChannel(out);

            long position = 0;
            long size = file.length();
            while (position < size) {
                long transferred = channel.transferTo(position, size - position, writableChannel);
                position += transferred;
            }
        }
    }
}

