package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;

@Slf4j
public class WriterTest {

    private static final String FILE_PATH = Objects.requireNonNull(
            ReaderTest.class.getResource("/io/OutputStreamTest.txt")
    ).getPath();

    public static void testOutputStreamWriter () {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(FILE_PATH))) {
            writer.write("testOutputStreamWriter");
            writer.flush();
//            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void testFileWriter () {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            writer.write("testFileWriter");
            writer.flush();
//            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void testBufferedWriter () {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("testBufferedWriter",0 , "testBufferedWriter".length());
            writer.flush();
//            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void testPrintWriter () {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("hello world");
            pw.printf("PI = %.4f%n", Math.PI);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    public static void main(String[] args) {
//        testOutputStreamWriter();
//        testFileWriter();
//        testBufferedWriter();
        testPrintWriter();
    }

}
