package com.chugyoyo.db.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLockHelper {

    private final StringRedisTemplate redisTemplate;

    public RedisLockHelper(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String key, String value, long expireSeconds) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS);
        return success != null && success;
    }

    /**
     * 释放锁（简单实现，注意安全性可加 Lua 脚本）
     */
    public void unlock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
