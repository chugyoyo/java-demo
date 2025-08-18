package com.chugyoyo.db.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class DistributedLockAspect {

    private final RedisLockHelper redisLockHelper;

    public DistributedLockAspect(RedisLockHelper redisLockHelper) {
        this.redisLockHelper = redisLockHelper;
    }

    @Around("@annotation(DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock lock = signature.getMethod().getAnnotation(DistributedLock.class);

        String key = lock.key();
        long expire = lock.expire();
        long waitTime = lock.waitTime();
        String lockValue = UUID.randomUUID().toString();

        long endTime = System.currentTimeMillis() + waitTime * 1000;
        boolean acquired = false;

        while (System.currentTimeMillis() < endTime) {
            acquired = redisLockHelper.tryLock(key, lockValue, expire);
            if (acquired) break;
            Thread.sleep(100); // 等待 100ms 再尝试
        }

        if (!acquired) {
            throw new RuntimeException("无法获取锁: " + key);
        }

        try {
            return joinPoint.proceed();
        } finally {
            redisLockHelper.unlock(key, lockValue);
        }
    }
}

