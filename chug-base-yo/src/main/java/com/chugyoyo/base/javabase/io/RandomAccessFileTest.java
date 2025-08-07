package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

@Slf4j
public class RandomAccessFileTest {

    private static final String OUTPUT_PATH = Objects.requireNonNull(
            RandomAccessFileTest.class.getResource("/io/OutputStreamTest.txt")
    ).getPath();

    private static final String INPUT_PATH = Objects.requireNonNull(
            RandomAccessFileTest.class.getResource("/io/InputStreamTest.txt")
    ).getPath();

    public static void testWrite() {
        try (RandomAccessFile raf = new RandomAccessFile(OUTPUT_PATH, "rw")) {
            // 写入数据
            raf.writeInt(100);
            raf.writeDouble(3.14159);
            raf.writeUTF("Java I/O");

            // 移动到文件开始
            raf.seek(0);

            // 读取数据
            System.out.println("Int: " + raf.readInt());
            System.out.println("Double: " + raf.readDouble());
            System.out.println("String: " + raf.readUTF());

            // 修改部分数据
            raf.seek(4); // 定位到 double 位置
            raf.writeBoolean(true);
            System.out.println("Boolean: " + raf.readBoolean());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        testWrite();
    }
}
