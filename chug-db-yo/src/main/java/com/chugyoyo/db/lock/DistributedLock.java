package com.chugyoyo.db.lock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    String key();           // 锁的 key
    long expire() default 30; // 锁过期时间，单位秒
    long waitTime() default 10; // 等待获取锁的时间，单位秒
}

