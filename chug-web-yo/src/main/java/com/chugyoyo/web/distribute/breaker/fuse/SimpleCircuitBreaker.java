package com.chugyoyo.web.distribute.breaker.fuse;

public class SimpleCircuitBreaker {

    enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int threshold = 3; // 连续失败次数阈值
    private final long openTimeout = 5000; // 熔断时间 5 秒
    private long lastOpenedTime = 0;

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // OPEN → HALF_OPEN
        if (state == State.OPEN && (now - lastOpenedTime) > openTimeout) {
            state = State.HALF_OPEN;
        }

        // 只要不是 OPEN 状态，就允许调用
        return state != State.OPEN;
    }

    public synchronized void recordSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }

    public synchronized void recordFailure() {
        failureCount++;
        if (failureCount >= threshold) {
            state = State.OPEN;
            lastOpenedTime = System.currentTimeMillis();
            System.out.println("🔥 熔断开启！");
        }
    }
}

