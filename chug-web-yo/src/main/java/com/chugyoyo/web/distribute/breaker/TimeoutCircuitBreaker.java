//package com.chugyoyo.web.distribute.breaker;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.RUNTIME)
//public @interface TimeoutCircuitBreaker {
//
//    long timeout() default 1000; // 默认超时时间1秒
//
//    String fallback() default ""; // 可选：降级方法名
//}
