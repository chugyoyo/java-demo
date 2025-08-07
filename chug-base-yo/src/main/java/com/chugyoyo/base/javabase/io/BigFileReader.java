package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

@Slf4j
public class BigFileReader {

    private final LongAdder longAdder = new LongAdder();

    private void readFileChunk(String filePath, long start, long chunkSize, Consumer<String> consumer) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            // 定位到分片起始位置
            raf.seek(start);

            byte[] buffer = new byte[8192];
            long bytesRead = 0;

            while (bytesRead < chunkSize) {
                int toRead = (int) Math.min(buffer.length, chunkSize - bytesRead);
                int read = raf.read(buffer, 0, toRead);
                if (read == -1) break;

                processChunk(buffer, read, consumer); // 处理分片数据
                bytesRead += read;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processChunk(byte[] buffer, int read, Consumer<String> consumer) {
        consumer.accept(new String(buffer, 0, read));
        longAdder.add(read);
    }

    public void parallelRead(String filePath, Consumer<String> consumer, ExecutorService executorService, Integer chunkNum) {

        ExecutorService executor = executorService != null ? executorService :
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        File file = new File(filePath);
        long fileSize = file.length();
        int chunks = chunkNum != null ? chunkNum : 8; // 分片数量
        long chunkSize = fileSize / chunks;

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < chunks; i++) {
            long start = i * chunkSize;
            long end = (i == chunks - 1) ? fileSize : start + chunkSize;

            futures.add(executor.submit(() -> {
                readFileChunk(filePath, start, end - start, consumer);
            }));
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(), e);
            }
        }
        // 验证是否 = fileSize
        long sum = longAdder.sum();
        assert sum == fileSize;
    }
}
