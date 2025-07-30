package com.chugyoyo.tool.javabase.lifecycle.init;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

// 类初始化阻塞优化
public class ClassInitBlockOptimizeTest {

    public static void main(String[] args) {

        // 多线程触发初始化
        long startTime = System.currentTimeMillis();
//        LocalCache.loadLocalCache(); // 【核心优化】预热，能快很多
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
                1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        CountDownLatch countDownLatch = new CountDownLatch(10);
        IntStream.range(0, 10).forEach(i -> {
            executor.execute(() -> {
                System.out.println("init start " + i);
                LocalCache localCache = new LocalCache();
                String s = localCache.get("" + i);
                System.out.println("key = " + i + ", value = " + s);
                System.out.println("init finish " + i);
                countDownLatch.countDown();
            });
        });

        // 等待所有任务完成
        try {
            executor.shutdown();
            countDownLatch.await();
            long endTime = System.currentTimeMillis();
            System.out.println("init cost time /ms: " + (endTime - startTime));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class LocalCache {

    public static volatile ConcurrentHashMap<String, String> localCache = null;

    static {
        loadLocalCache();
    }

    public static void loadLocalCache() {
        if (localCache == null) {
            synchronized (LocalCache.class) {
                if (localCache == null) {
                    try {
                        Thread.sleep(5000); // 模拟加载很久的数据
                        localCache = new ConcurrentHashMap<>();
                        localCache.put("1", "10");
                        localCache.put("2", "20");
                        localCache.put("3", "30");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public String get(String key) {
        if (localCache == null) {
            loadLocalCache();
        }
        return localCache.get(key);
    }
}