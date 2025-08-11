package com.chugyoyo.base.javabase.exception;

public class TryWithResourcesTest {

    public static void main(String[] args) {
        try (AutoCloseableException r = new AutoCloseableException()) {
            throw new RuntimeException("主异常");
        } catch (Exception e) {
            e.printStackTrace();
//            for (Throwable suppressed : e.getSuppressed()) {
//                System.out.println("Suppressed: " + suppressed);
//            }
        }
    }
}

class AutoCloseableException implements AutoCloseable {
    @Override
    public void close() {
        throw new RuntimeException("关闭异常");
    }
}
