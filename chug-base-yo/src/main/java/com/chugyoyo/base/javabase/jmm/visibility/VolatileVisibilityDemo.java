package com.chugyoyo.base.javabase.jmm.visibility;

import java.util.concurrent.TimeUnit;

/**
 * 可见性问题演示：主线程修改标志位后，工作线程无法感知
 * <p>
 * 使用 volatile 解决可见性问题
 */
public class VolatileVisibilityDemo {

    // 关键变量：不使用 volatile 会导致可见性问题
    private static
//    volatile
            boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        // 工作线程：监控 running 标志位
        Thread worker = new Thread(() -> {
            System.out.println("Worker started. Monitoring running flag...");

            int counter = 0;
            // 当 running 为 true 时持续运行
            while (running) {
                counter++;
                // 模拟工作负载
            }

            System.out.printf("Worker stopped. Counter: %,d%n", counter);
        });

        worker.start();

        // 主线程等待 1 秒
        TimeUnit.SECONDS.sleep(1);

        System.out.println("Main thread changing running to false...");
        // 修改运行标志
        running = false;

        // 等待工作线程结束
        worker.join();
        System.out.println("Main thread completed.");
    }
}
