package com.chugyoyo.db.service;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class RedissonDelayedTaskTest {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient client = Redisson.create(config);

        RBlockingQueue<String> blockingQueue = client.getBlockingQueue("delay-queue");
        RDelayedQueue<String> delayedQueue = client.getDelayedQueue(blockingQueue);


        // 一个消费者
        new Thread(() -> {
            while (true) {
                try {
                    System.err.println("\n\n curTime=" + LocalDateTime.now() + " receive message : " + blockingQueue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 发送延时消息
        for (int i = 0; i < 5; i++) {
            delayedQueue.offer("msg-" + i, 10, TimeUnit.SECONDS);
            System.err.println("\n\n curTime=" + LocalDateTime.now() + " send message : " + i);
        }
    }

}
