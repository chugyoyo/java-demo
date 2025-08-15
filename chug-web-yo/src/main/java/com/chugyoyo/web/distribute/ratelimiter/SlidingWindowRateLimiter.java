package com.chugyoyo.web.distribute.ratelimiter;

import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowRateLimiter {

    // 窗口大小（毫秒）
    private final long windowSizeInMillis;
    // 窗口内最大请求数
    private final int maxRequests;
    // 保存请求时间戳
    private final Queue<Long> requestTimestamps;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.requestTimestamps = new LinkedList<>();
    }

    // 是否允许访问
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // 1. 移除过期请求
        while (!requestTimestamps.isEmpty() && (now - requestTimestamps.peek()) >= windowSizeInMillis) {
            requestTimestamps.poll();
        }

        // 2. 判断是否超过限制
        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.add(now);
            return true; // 放行
        } else {
            return false; // 拒绝
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 示例：1秒内最多允许3次请求
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 1000);

        for (int i = 1; i <= 10; i++) {
            if (limiter.allowRequest()) {
                System.out.println("第 " + i + " 次请求  允许");
            } else {
                System.out.println("第 " + i + " 次请求  拒绝");
            }
            Thread.sleep(300); // 每隔300ms请求一次
        }
    }
}