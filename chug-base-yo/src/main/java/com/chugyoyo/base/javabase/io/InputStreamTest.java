package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class InputStreamTest {

    // 必须加上斜杠，不然会找不到资源
    private static final String FILE_PATH = Objects.requireNonNull(
            InputStreamTest.class.getResource("/io/InputStreamTest.txt")
    ).getPath();

    private static final String OUTPUT_FILE_PATH = Objects.requireNonNull(
            InputStreamTest.class.getResource("/io/OutputStreamTest.txt")
    ).getPath();

    public static void testFileInputStream() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(FILE_PATH);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        long start = System.currentTimeMillis();
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                // 处理读取的数据
                log.info("read {} bytes", bytesRead);
                log.info("read data: {}", new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        log.info("read cost {} ms", end - start);
    }

    public static void testBufferedInputStream() {
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(FILE_PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        long start = System.currentTimeMillis();
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                // 处理读取的数据
                log.info("read {} bytes", bytesRead);
                log.info("read data: {}", new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        log.info("read cost {} ms", end - start);
    }

    public static void testDataInputStream() {
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new FileInputStream(FILE_PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        long start = System.currentTimeMillis();
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                // 处理读取的数据
                log.info("read {} bytes", bytesRead);
                log.info("read data: {}", new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis();
        log.info("read cost {} ms", end - start);
    }

    public static void testObjectInputStream() {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(OUTPUT_FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Object object = objectInputStream.readObject();
            log.info("read object {} class {}", object, object.getClass());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        testFileInputStream();
//        testBufferedInputStream();
//        testDataInputStream();
        testObjectInputStream();
    }
}
