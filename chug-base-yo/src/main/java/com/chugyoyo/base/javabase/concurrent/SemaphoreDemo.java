package com.chugyoyo.base.javabase.concurrent;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    // 最多允许3个线程同时访问
    private static final Semaphore semaphore = new Semaphore(3);

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    // 获取一个许可（可能阻塞）
                    semaphore.acquire();
                    System.out.println("Worker " + workerId + " acquired a permit");
                    Thread.sleep(1000); // 模拟业务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 释放许可
                    semaphore.release();
                    System.out.println("Worker " + workerId + " released a permit");
                }
            }).start();
        }
    }
}

