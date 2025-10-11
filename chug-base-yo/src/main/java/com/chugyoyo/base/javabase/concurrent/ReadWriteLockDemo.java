package com.chugyoyo.base.javabase.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ReadWriteLockDemo {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Object> cache = new HashMap<>();

    // 读操作：多个线程可同时读
    public Object get(String key) {
        lock.readLock().lock();
        try {
            // 模拟读取时间
            Thread.sleep(200);
            return cache.get(key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    // 写操作：写时独占，阻塞其他读写
    public void put(String key, Object value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 正在写入 " + key);
            Thread.sleep(500);
            cache.put(key, value);
            System.out.println(Thread.currentThread().getName() + " 写入完成 " + key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReadWriteLockDemo demo = new ReadWriteLockDemo();
        ExecutorService pool = Executors.newFixedThreadPool(5);

        // 初始化缓存
        demo.put("A", "初始值");

        // 模拟多个线程读 + 写
        Runnable readTask = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " 开始读取");
            Object value = demo.get("A");
            System.out.println(threadName + " 读取到: " + value);
        };

        Runnable writeTask = () -> {
            String threadName = Thread.currentThread().getName();
            demo.put("A", threadName + " 写入的新值");
        };

        // 提交多个读任务和写任务
        pool.submit(readTask);
        pool.submit(readTask);
        pool.submit(writeTask);
        pool.submit(readTask);
        pool.submit(writeTask);

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
