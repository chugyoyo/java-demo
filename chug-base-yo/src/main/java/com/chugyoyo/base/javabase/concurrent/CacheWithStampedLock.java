package com.chugyoyo.base.javabase.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

public class CacheWithStampedLock {

    private final Map<String, String> cache = new HashMap<>();
    private final StampedLock lock = new StampedLock();

    // 模拟从数据库加载数据
    private String loadFromDB(String key) {
        try {
            Thread.sleep(50); // 模拟慢IO
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Value-" + key + "-" + System.nanoTime();
    }

    // 缓存读取方法（含乐观读）
    public String get(String key) {
        long stamp = lock.tryOptimisticRead();
        String value = cache.get(key);

        // 如果缓存命中并且没有被写干扰
        if (value != null && lock.validate(stamp)) {
            return value;
        }

        // 校验失败或缓存未命中 -> 升级为悲观读锁
        stamp = lock.readLock();
        try {
            value = cache.get(key);
            if (value != null) {
                return value;
            }
        } finally {
            lock.unlockRead(stamp);
        }

        // 缓存未命中 -> 加写锁更新
        long writeStamp = lock.writeLock();
        try {
            // Double check 避免重复加载
            value = cache.get(key);
            if (value == null) {
                value = loadFromDB(key);
                cache.put(key, value);
            }
            return value;
        } finally {
            lock.unlockWrite(writeStamp);
        }
    }

    // 主测试程序
    public static void main(String[] args) throws InterruptedException {
        CacheWithStampedLock cache = new CacheWithStampedLock();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                String v = cache.get("user:1");
                System.out.println(Thread.currentThread().getName() + " -> " + v);
            }
        };

        // 并发读相同 key
        for (int i = 0; i < 10; i++) {
            pool.submit(task);
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("全部任务结束");
    }
}
