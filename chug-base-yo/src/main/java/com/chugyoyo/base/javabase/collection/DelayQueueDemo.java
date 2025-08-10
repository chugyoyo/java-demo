package com.chugyoyo.base.javabase.collection;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueueDemo {

    public static void main(String[] args) {
        // 使用
        DelayQueue<DelayedTask> taskQueue = new DelayQueue<>();
        // 生产者线程: 添加一个5秒后执行的任务
        taskQueue.put(new DelayedTask(() -> System.out.println("Task executed!"), 5, TimeUnit.SECONDS));

        // 消费者线程 (通常是一个或多个工作线程)
        while (true) {
            try {
                DelayedTask task = taskQueue.take(); // 阻塞直到有任务到期
                task.runTask(); // 执行到期任务
            } catch (InterruptedException e) {
                // 处理中断
                break;
            }
        }
    }

    public static class DelayedTask implements Delayed {
        private final long expireTime; // 绝对到期时间戳 (纳秒)
        private final Runnable task;

        DelayedTask(Runnable task, long delay, TimeUnit unit) {
            this.task = task;
            this.expireTime = System.nanoTime() + unit.toNanos(delay);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expireTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            return Long.compare(this.expireTime, ((DelayedTask) other).expireTime);
        }

        public void runTask() {
            task.run();
        }
    }
}
