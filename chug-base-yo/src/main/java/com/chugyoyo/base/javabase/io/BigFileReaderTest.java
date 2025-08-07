package com.chugyoyo.base.javabase.io;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class BigFileReaderTest {

    public static void main(String[] args) {
        BigFileReader bigFileReader = new BigFileReader();
        String filePath = Objects.requireNonNull(
                ReaderTest.class.getResource("/io/InputStreamTest.txt")
        ).getPath();
        bigFileReader.parallelRead(filePath, line -> {
            log.info("line: {}", line);
        }, null, 3);
    }
}
