package com.chugyoyo.base.javabase.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 一个基于 AQS 的简单独占锁（非重入）。
 */
public class SimpleAQSLockDemo {

    /**
     * SimpleAQSLock: 一个简单的独占锁实现（非重入）。
     */
    public static class SimpleAQSLock implements Lock {

        /**
         * 内部同步器：state == 0 表示未被占用，state == 1 表示被占用。
         */
        private static class Sync extends AbstractQueuedSynchronizer {
            private static final long serialVersionUID = 1L;

            /**
             * 尝试独占式获取，成功则返回 true。
             * 这里实现为非重入锁：如果 state != 0 则获取失败（即使 owner 是当前线程也失败）。
             */
            @Override
            protected boolean tryAcquire(int acquires) {
                // only allow acquires == 1 in this simple implementation
                if (acquires != 1) throw new IllegalArgumentException();
                // fast path: state == 0 -> try to CAS to 1
                if (compareAndSetState(0, 1)) {
                    // record owner thread for isHeldExclusively / debug
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
                return false;
            }

            /**
             * 释放锁：将 state 置 0，并清除 owner。
             * 返回 true 表示已经完全释放（对独占锁总是 true）。
             */
            @Override
            protected boolean tryRelease(int releases) {
                if (releases != 1) throw new IllegalArgumentException();
                if (getState() == 0) throw new IllegalMonitorStateException();
                // clear owner and state
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }

            @Override
            protected boolean isHeldExclusively() {
                return getState() == 1 && getExclusiveOwnerThread() == Thread.currentThread();
            }

            Condition newCondition() {
                return new ConditionObject();
            }
        }

        private final Sync sync = new Sync();

        @Override
        public void lock() {
            sync.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            return sync.tryAcquire(1);
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return sync.tryAcquireNanos(1, unit.toNanos(time));
        }

        @Override
        public void unlock() {
            if (sync.release(1)) {
                // nothing else to do;
                // AQS will unpark successor in release()
            } else {
                throw new IllegalMonitorStateException();
            }
        }

        @Override
        public Condition newCondition() {
            return sync.newCondition();
        }
    }

    /*****************************
     * Demo: 多线程竞争 SimpleAQSLock
     *****************************/
    public static void main(String[] args) throws InterruptedException {
        final SimpleAQSLock lock = new SimpleAQSLock();

        // 线程任务：尝试加锁，持有 1s，然后释放
        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            try {
                System.out.println(name + " 尝试获取锁...");
                lock.lock();
                try {
                    System.out.println(name + " 获取到锁，doing work...");
                    // 模拟工作
                    Thread.sleep(1000);
                } finally {
                    lock.unlock();
                    System.out.println(name + " 释放了锁。");
                }
            } catch (InterruptedException e) {
                System.out.println(name + " 被中断。");
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");
        Thread t3 = new Thread(() -> {
            String name = Thread.currentThread().getName();
            try {
                System.out.println(name + " 尝试用 tryLock(5000ms) 获取锁...");
                boolean ok = lock.tryLock(5000, TimeUnit.MILLISECONDS);
                if (ok) {
                    try {
                        System.out.println(name + " tryLock 成功，做短任务...");
                    } finally {
                        lock.unlock();
                        System.out.println(name + " tryLock 释放锁。");
                    }
                } else {
                    System.out.println(name + " tryLock 超时，放弃。");
                }
            } catch (InterruptedException e) {
                System.out.println(name + " 被中断。");
                Thread.currentThread().interrupt();
            }
        }, "T3");

        t1.start();
        Thread.sleep(50); // 小间隔，制造抢占
        t2.start();
        Thread.sleep(50);
        t3.start();
        Thread t4 = new Thread(task, "T4");
        t4.start();
        Thread t5 = new Thread(task, "T5");
        t5.start();
        Thread t6 = new Thread(task, "T6");
        t6.start();
        Thread t7 = new Thread(task, "T7");
        t7.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();

        System.out.println("演示结束");
    }
}

