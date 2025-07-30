package com.chugyoyo.web.distribute.stress;

import com.chugyoyo.web.distribute.id.IdWorker;
import com.chugyoyo.web.distribute.id.SnowFlake;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

/**
 * 压测（Stress Test）工具，类似 Java JMeter 压测工具，可以测试本地方法，防止网络等因素对结果的影响
 * 压测参数：
 * - Number of Threads (users)：设置 N 个并发用户
 * - Ramp-up period (seconds)：N 秒内启动所有用户，给被压测系统预热的时间
 * - Loop Count：重复 N 轮
 * 压测结果指标：
 * - Throughput (QPS)：吞吐量，每秒处理的请求数
 * - Error Rate：错误率，请求出错的比例
 * - Average Response Time：平均响应时间，请求的平均处理时间
 *
 * @author chugyoyo
 * @since 2025/7/12
 */
public class StressTestUtils {

    @Builder
    @Data
    private static class StressTest {

        private StatisticResult statisticResult;

        private Config config;

        private Supplier<Boolean> task;

        private void run() {
            // 初始化统计结果
            statisticResult = StatisticResult.builder()
                    .errorCount(new LongAdder())
                    .sampleCount(new LongAdder())
                    .totalResponseTime(new LongAdder())
                    .successCount(new LongAdder())
                    .build();

            int allCount = config.numberOfThreads * config.loopCount;
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    config.numberOfThreads, config.numberOfThreads,
                    30L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(config.numberOfThreads * config.loopCount),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            int prestartAllCoreThreads = threadPoolExecutor.prestartAllCoreThreads();
            System.out.println("finish prestartAllCoreThreads: " + prestartAllCoreThreads);
            CountDownLatch countDownLatch = new CountDownLatch(allCount);
            long startNanoTime = System.nanoTime();
            for (int i = 0; i < config.numberOfThreads; i++) {
                threadPoolExecutor.submit(() -> {
                    for (int j = 0; j < config.loopCount; j++) {
                        long start = System.nanoTime();
                        boolean success = false;
                        try {
                            success = task.get();
                        } catch (Exception e) {
                            success = false;
                        } finally {
                            // 计算响应时间
                            long duration = System.nanoTime() - start;
                            // 记录
                            statisticResult.recordResult(duration, success);
                            countDownLatch.countDown();
                        }
                    }
                });
            }

            try {
                countDownLatch.await();
                long endNanoTime = System.nanoTime();
                statisticResult.setEndNanoTime(endNanoTime);
                statisticResult.setStartNanoTime(startNanoTime);
                // 输出结果
                statisticResult.statisticAndPrint();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                threadPoolExecutor.shutdown();
            }
        }
    }

    /**
     * 统计结果
     */
    @Data
    @Builder
    private static class StatisticResult {
        /**
         * Error Count：错误数量
         */
        private LongAdder errorCount;
        /**
         * Success Count：成功数量
         */
        private LongAdder successCount;
        /**
         * Sample Count：样本数量
         */
        private LongAdder sampleCount;
        /**
         * Total Response Time：总响应时间 /ns
         */
        private LongAdder totalResponseTime;

        private Long startNanoTime;
        private Long endNanoTime;

        private synchronized void statisticAndPrint() {
            // Throughput (QPS)：吞吐量，每秒处理的请求数
            BigDecimal qps = (
                    new BigDecimal(this.successCount.sum())
                            .multiply(new BigDecimal(1000_000_000))
                            .divide(new BigDecimal(this.endNanoTime - this.startNanoTime), 2, RoundingMode.HALF_UP)
            );
            // Error Rate：错误率，请求出错的比例
            BigDecimal errorRate = (
                    new BigDecimal(this.errorCount.sum())
                            .multiply(new BigDecimal(100))
                            .divide(new BigDecimal(this.sampleCount.sum()), 10, RoundingMode.HALF_UP)
            );
            // Average Response Time：平均响应时间 /ms，请求的平均处理时间
            BigDecimal averageResponseTime = (
                    new BigDecimal(this.totalResponseTime.longValue())
                            .divide(new BigDecimal(this.successCount.sum()), 10, RoundingMode.HALF_UP)
                            .divide(new BigDecimal(1000_000L), 10, RoundingMode.HALF_UP)
            );
            System.out.println("===result:===");
            System.out.println("Sample Count：" + sampleCount.sum());
            System.out.println("Error Count：" + errorCount.sum());
            System.out.println("Error Rate：" + errorRate + " %");
            System.out.println("Throughput：" + qps + " QPS");
            System.out.println("Average Response Time：" + averageResponseTime + " ms");
        }

        public void recordResult(Long duration, boolean success) {
            this.sampleCount.increment();
            if (success) {
                this.successCount.increment();
                this.totalResponseTime.add(duration);
            } else {
                this.errorCount.increment();
            }
        }
    }

    /**
     * 配置参数
     */
    @Data
    @Builder
    private static class Config {
        /**
         * Number of Threads (users)：设置 N 个并发用户
         */
        private Integer numberOfThreads;
        /**
         * Ramp-up period (seconds)：N 秒内启动所有用户，给被压测系统预热的时间
         */
        private Integer rampUpPeriod;
        /**
         * Loop Count：重复 N 轮
         */
        private Integer loopCount;
    }


    /**
     * 入口函数
     *
     * @param task
     * @param numberOfThreads
     * @param rampUpPeriod
     * @param loopCount
     */
    public static void test(Supplier<Boolean> task, Integer numberOfThreads, Integer rampUpPeriod, Integer loopCount) {
        StressTest.builder().config(Config.builder()
                        .numberOfThreads(numberOfThreads)
                        .rampUpPeriod(rampUpPeriod)
                        .loopCount(loopCount)
                        .build())
                .task(task)
                .build()
                .run();
    }

    public static void main(String[] args) {
        IdWorker idWorker = new IdWorker(null);
        SnowFlake snowFlake = new SnowFlake(1, 1);
        test(() -> {
            try {
//                idWorker.nextId();
                snowFlake.nextId();
                return true;
            } catch (Exception e) {
                return false;
            }
        }, 2000, 5, 10000);
    }
}
