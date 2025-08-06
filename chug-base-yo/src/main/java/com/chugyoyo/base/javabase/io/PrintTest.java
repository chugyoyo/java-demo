package com.chugyoyo.base.javabase.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class PrintTest {

    public static void testPrintStream() {
        try (PrintStream ps = new PrintStream(new FileOutputStream("print.txt"))) {
            ps.println("文本内容");
            ps.printf("格式输出: %d, %.2f, %s%n", 100, 3.1415, "Java");
            ps.print(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // PrintStream
        System.out.println("hello world");
    }
}
