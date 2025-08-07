package com.chugyoyo.base.javabase.jmm;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NoVolatileSingletonTest {

    public static void main(String[] args) {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < 20000; i++) {
            log.info("第 {} 轮测试", i);
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);
            for (int j = 0; j < threadCount; j++) {
                int finalJ = j;
                int finalI = i;
                executorService.submit(() -> {
                    // 所有线程互相等待，同时开始
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    // 线程同时获取 NoVolatileSingleton，指令重排序可能导致 instance 未初始化
                    NoVolatileSingleton instance = NoVolatileSingleton.getInstance();
                    if (!instance.isAllInit()) {
                        log.error("对象第 {} 轮第 {} 次时未初始化完成", finalI, finalJ);
                        System.exit(0);
                    }
                    countDownLatch.countDown();
                });
            }
            try {
                // 等待所有线程结束
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            NoVolatileSingleton.clearInstance();
        }
        executorService.shutdown();
    }
}
