package com.chugyoyo.base.javabase.jmm;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.PriorityQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class VolatileExampleTest {

    @ToString
    private static class Result {
        BigDecimal nanoTime;
        int value;
        boolean isGet = false;

        public Result(Long nanoTime, int value, boolean isGet) {
            this.nanoTime = new BigDecimal(nanoTime);
            this.value = value;
            this.isGet = isGet;
        }
    }

    // TODO 没有复现可见性问题？why？
    public static void main(String[] args) {

        PriorityQueue<Result> priorityQueue = new PriorityQueue<>((a, b) -> a.nanoTime.compareTo(b.nanoTime));
        int getThreadNum = 10;
        int totalThreadNum = getThreadNum + 1;
        ExecutorService threadPool = Executors.newFixedThreadPool(getThreadNum + 1);
        CountDownLatch countDownLatch = new CountDownLatch(totalThreadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(totalThreadNum);
        VolatileExample volatileExample = new VolatileExample();
        threadPool.submit(() -> {
            log.info("线程1");
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < 10000; i++) {
                log.info("线程1 执行 i = {}", i);
                volatileExample.value = i;
//                log.info("当前时间：{}，线程1设置的值为：{}", System.nanoTime(), i);
                priorityQueue.add(new Result(System.nanoTime(), i, false));
            }
            countDownLatch.countDown();
        });
        for (int j = 0; j < getThreadNum; j++) {
            int finalJ = j;
            threadPool.submit(() -> {
                log.info("线程 {}", finalJ);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < 10000; i++) {
                    log.info("线程{} 执行 i = {}", finalJ, i);
                    long nanoTime = System.nanoTime();
                    int value = volatileExample.value;
//                log.info("当前时间：{}，线程2获取到的值为：{}", nanoTime, value);
                    priorityQueue.add(new Result(nanoTime, value, true));
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
            log.info("多线程执行完毕");
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        threadPool.shutdown();

        if (!priorityQueue.isEmpty()) {
            Result result1 = priorityQueue.poll();
            while (!priorityQueue.isEmpty()) {
                Result result2 = priorityQueue.poll();
                if (
                        !result1.isGet && result2.isGet
                                &&
                                result1.nanoTime.compareTo(result2.nanoTime) < 0
                                && result1.value > result2.value
                ) {
                    log.info("出现【指令重排序】导致的异常：result1={}, result2={}", result1, result2);
                }
                result1 = result2;
            }
        }

        log.info("测试完成");
    }
}
