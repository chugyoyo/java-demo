//package com.chugyoyo.breaker;
//
//@Service
//public class PaymentService {
//
//    // 设置超时时间为800ms，超时后调用fallback方法
//    @TimeoutCircuitBreaker(timeout = 800, fallback = "processPaymentFallback")
//    public String processPayment(String orderId) throws InterruptedException {
//        // 模拟耗时操作（实际可能调用第三方支付）
//        Thread.sleep(new Random().nextInt(1000)); // 随机延迟0-1秒
//        return "Payment processed for: " + orderId;
//    }
//
//    // 降级方法（参数需与原方法一致）
//    public String processPaymentFallback(String orderId) {
//        return "[Fallback] Payment processing delayed for: " + orderId;
//    }
//}