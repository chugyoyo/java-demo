package com.chugyoyo.base.javabase.concurrent;

public class DeadlockExample {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread-1 持有 lockA，尝试获取 lockB...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                synchronized (lockB) {
                    System.out.println("Thread-1 获取 lockB 成功");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread-2 持有 lockB，尝试获取 lockA...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                synchronized (lockA) {
                    System.out.println("Thread-2 获取 lockA 成功");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
