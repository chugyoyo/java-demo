package com.chugyoyo.base.javabase.jmm.atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class IPlusPlusTest {

    private int i = 0;

    public int getI() {
        return i;
    }

    public void increment() {
        i++;
    }

    public static void test1 () {
        IPlusPlusTest iPlusPlusTest = new IPlusPlusTest();
        int threadNum = Runtime.getRuntime().availableProcessors() * 8;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        log.info("threadNum:{}", threadNum);
        int totalTestRound = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(totalTestRound * threadNum);
        log.info("expect final i:{}", threadNum * totalTestRound);
        for (int i = 0; i < totalTestRound; i++) {
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
            for (int j = 0; j < threadNum; j++) {
                executorService.submit(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    iPlusPlusTest.increment();
                    countDownLatch.countDown();
                });
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("final i: {}", iPlusPlusTest.getI());
        executorService.shutdown();
    }

    public static void test2 () {
        for (int i = 0; i < 200; i++) {

        }
    }

    public static void main(String[] args) {

    }


}
