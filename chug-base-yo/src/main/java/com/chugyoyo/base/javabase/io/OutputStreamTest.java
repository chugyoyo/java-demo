package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Objects;

@Slf4j
public class OutputStreamTest {

    // 必须加上斜杠，不然会找不到资源
    private static final String FILE_PATH = Objects.requireNonNull(
            OutputStreamTest.class.getResource("/io/OutputStreamTest.txt")
    ).getPath();

    public static void testFileOutputStream() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            byte[] bytes = "Hello, World!".getBytes();
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static void testBufferedOutputStream() {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            byte[] bytes = "Hello, World!".getBytes();
            bufferedOutputStream.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static void testDataOutputStream() {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            dataOutputStream.writeChars("Hello, World!");
            dataOutputStream.writeInt(100);
            dataOutputStream.writeBoolean(true);
            dataOutputStream.writeDouble(3.14);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static void testObjectOutputStream() {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            IoTestEntity ioTestEntity = new IoTestEntity(100, "test 100");
            objectOutputStream.writeObject(ioTestEntity);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static void testPrintStream() {
        try (PrintStream ps = new PrintStream(new FileOutputStream(FILE_PATH))) {
            ps.println("hello world");
            ps.printf("格式输出: %d, %.2f, %s%n", 100, 3.1415, "Java");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
//        testFileOutputStream();
//        testBufferedOutputStream();
//        testDataOutputStream();
//        testObjectOutputStream();
        testPrintStream();
    }
}
