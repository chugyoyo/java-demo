package com.chugyoyo.base.javabase.jmm.reorder;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MultiCalTest {

    // 初始状态
    int a = 0;
    boolean flag = false;

    public void method1() {
        a = new Random().nextInt(100) + 1; // 语句1
        flag = true;    // 语句2
    }

    public void method2() {
        if (flag) {     // 语句3
            int b = a;  // 语句4
            if (b == 0) {
                log.info("a 未初始化，发生了指令重排序");
            }
        }
    }

    /**
     * 测试不出指令重排序
     *
     * @param args
     */
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 1000000; i++) {
            MultiCalTest multiCalTest = new MultiCalTest();
            CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
            CountDownLatch countDownLatch = new CountDownLatch(2);
            threadPool.submit(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                multiCalTest.method1();
                countDownLatch.countDown();
            });
            threadPool.submit(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                multiCalTest.method2();
                countDownLatch.countDown();
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        threadPool.shutdown();
    }

}
