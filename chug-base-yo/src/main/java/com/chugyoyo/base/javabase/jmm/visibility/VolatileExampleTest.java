package com.chugyoyo.base.javabase.jmm.visibility;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class VolatileExampleTest {

    /**
     * 没有复现可见性问题，说明一般情况下，线程共享的变量不会有可见性问题
     * <p>
     * 不管是 static 还是 普通成员变量，都是这样
     * <p>
     * 不过，{@link VolatileVisibilityDemo} 这个demo 才是复现可见性问题的场景
     */
    public static void main(String[] args) {

        int getThreadNum = 1;
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
//            volatileExample.setRunning(false);
            VolatileExample.isRunning2 = false;
            log.info("线程1 执行完毕，暂停所有任务");
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
//                for (int i = 0; i < 10000; i++) {
//                    if (!volatileExample.isRunning() || !VolatileExample.isRunning2) {
//                        break;
//                    }
//                    log.info("线程{} 执行 i = {}", finalJ, i);
//                }
                while (volatileExample.isRunning() && VolatileExample.isRunning2) {
//                    log.info("线程{} 执行", finalJ);
                }
                log.info("线程{} 结束执行", finalJ);
                countDownLatch.countDown();
            });
        }

//        VolatileExample.isRunning2 = false;

        try {
            countDownLatch.await();
            log.info("多线程执行完毕");
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        threadPool.shutdown();

        log.info("测试完成");
    }
}
