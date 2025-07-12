package com.chugyoyo.stress;

import com.chugyoyo.id.IdWorker;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
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
                    .qps(new AtomicReference<>(BigDecimal.ZERO))
                    .errorRate(new AtomicReference<>(BigDecimal.ZERO))
                    .averageResponseTime(new AtomicReference<>(BigDecimal.ZERO))
                    .errorCount(new LongAdder())
                    .sampleCount(new LongAdder())
                    .totalResponseTime(new AtomicLong(0))
                    .successCount(new LongAdder())
                    .build();

            // 定时输出 5s 输出 1 次
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
            scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
                statisticResult.statisticAndPrint();
            }, 5, 5, TimeUnit.SECONDS);

            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    config.numberOfThreads, config.numberOfThreads,
                    30L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(config.numberOfThreads * config.loopCount),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            int prestartAllCoreThreads = threadPoolExecutor.prestartAllCoreThreads();
            System.out.println("prestartAllCoreThreads: " + prestartAllCoreThreads);
            for (int i = 0; i < config.numberOfThreads; i++) {
                threadPoolExecutor.submit(() -> {
                    for (int j = 0; j < config.loopCount; j++) {
                        long start = System.nanoTime();
                        boolean success = false;
                        try {
                            success = task.get();
                        } catch (Exception e) {
                            success = false;
                        }
                        // 计算响应时间
                        long duration = System.nanoTime() - start;
                        // 记录
                        statisticResult.recordResult(duration, success);
                    }
                });
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
         * Throughput (QPS)：吞吐量，每秒处理的请求数
         */
        private AtomicReference<BigDecimal> qps;
        /**
         * Error Rate：错误率，请求出错的比例
         */
        private AtomicReference<BigDecimal> errorRate;
        /**
         * Average Response Time：平均响应时间 /ms，请求的平均处理时间
         */
        private AtomicReference<BigDecimal> averageResponseTime;
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
        private AtomicLong totalResponseTime;
        /**
         * Max Response Time：最大响应时间 /ns
         */
        private AtomicReference<BigDecimal> maxResponseTime;
        /**
         * Min Response Time：最小响应时间 /ns
         */
        private AtomicReference<BigDecimal> minResponseTime;

        private void statisticAndPrint() {
            this.qps.getAndSet(
                    new BigDecimal(this.successCount.sum())
                            .multiply(new BigDecimal(1000_000_000))
                            .divide(new BigDecimal(this.totalResponseTime.get()), 2, RoundingMode.HALF_UP)
            );
            this.errorRate.getAndSet(
                    new BigDecimal(this.errorCount.sum())
                            .multiply(new BigDecimal(100))
                            .divide(new BigDecimal(this.sampleCount.sum()), 10, RoundingMode.HALF_UP)
            );
            this.averageResponseTime.getAndSet(
                    new BigDecimal(this.totalResponseTime.get())
                            .divide(new BigDecimal(this.successCount.sum()), 10, RoundingMode.HALF_UP)
                            .divide(new BigDecimal(1000_000L), 10, RoundingMode.HALF_UP)
            );

            System.out.println("===result:===");
            System.out.println("Throughput：" + qps.get() + " QPS");
            System.out.println("Error Rate：" + errorRate.get() + " %");
            System.out.println("Average Response Time：" + averageResponseTime.get() + " ms");
            System.out.println("Error Count：" + errorCount.sum());
            System.out.println("Sample Count：" + sampleCount.sum());
            System.out.println("Total Response Time：" + totalResponseTime.get() + " ns");
        }

        public void recordResult(Long duration, boolean success) {
            this.sampleCount.increment();
            if (success) {
                this.successCount.increment();
                this.totalResponseTime.getAndAdd(duration);
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
        test(() -> {
            try {
                idWorker.nextId();
                return true;
            } catch (Exception e) {
                return false;
            }
        }, 2000, 5, 1000000);
    }
}
