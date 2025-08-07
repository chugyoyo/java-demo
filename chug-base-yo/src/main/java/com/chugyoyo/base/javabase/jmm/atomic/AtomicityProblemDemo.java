package com.chugyoyo.base.javabase.jmm.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 原子性问题演示：多线程同时修改计数器导致结果错误
 */
public class AtomicityProblemDemo {

    private static int count = 0; // 普通计数器

    public static void main(String[] args) throws InterruptedException {
        // 创建线程池（4个线程）
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 提交1000个任务，每个任务对计数器加1000次
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 1000; j++) {
                    increment(); // 非原子操作
                }
            });
        }

        // 等待所有任务完成
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // 预期结果：1000 * 1000 = 1,000,000
        System.out.println("Final count: " + count);
    }

    private static void increment() {
        count++; // 非原子操作
    }
}
