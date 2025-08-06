package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

@Slf4j
public class ReaderTest {

    private static final String FILE_PATH = Objects.requireNonNull(
            ReaderTest.class.getResource("/io/InputStreamTest.txt")
    ).getPath();

    public static void testInputStreamReader() {
        try (Reader reader = new InputStreamReader(new FileInputStream(FILE_PATH))) {
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                String content = new String(buffer, 0, charsRead);
                log.info("read {} chars", charsRead);
                log.info("content:{}", content);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void testFileReader() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                String content = new String(buffer, 0, charsRead);
                log.info("read {} chars", charsRead);
                log.info("content:{}", content);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void testBufferedReader() {
        try (Reader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                String content = new String(buffer, 0, charsRead);
                log.info("read {} chars", charsRead);
                log.info("content:{}", content);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        testInputStreamReader();
        testFileReader();
        testBufferedReader();
    }
}
