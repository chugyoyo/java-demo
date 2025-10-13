package com.chugyoyo.web.distribute.breaker.fuse;

public class SimpleCircuitBreakerTest {

    public static void main(String[] args) throws InterruptedException {
        SimpleCircuitBreaker breaker = new SimpleCircuitBreaker();

        for (int i = 1; i <= 100; i++) {
            if (!breaker.allowRequest()) {
                System.out.println("请求被熔断，直接失败！");
                continue;
            }

            try {
                // 模拟下游调用
                if (Math.random() < 0.6) throw new RuntimeException("调用失败");
                System.out.println("✅ 调用成功");
                breaker.recordSuccess();
            } catch (Exception e) {
                System.out.println("❌ 调用异常");
                breaker.recordFailure();
            }

            Thread.sleep(1000);
        }

    }
}
