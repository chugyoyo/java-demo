package com.chugyoyo.db.controller;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/test_rate_limiter")
@RestController
public class TestRateLimiter {

    @Resource
    private RedissonClient redissonClient;

    @GetMapping("/test_rate_limiter")
    public void testRateLimiter() {
        String redisKey = "chugyoyo:test_rate_limiter";
        RRateLimiter limiter = redissonClient.getRateLimiter(redisKey);
        // 判断限流器是否已设置过限流规则，避免每次都重新设置
        if (!limiter.isExists()) {
            // 先删除不然设置不生效
            limiter.delete();
            // 限流：每秒最多100个请求
            limiter.trySetRate(RateType.OVERALL, 100, 1000, RateIntervalUnit.MILLISECONDS);
        }
        // 尝试获取1个令牌，返回true表示获取成功，返回false表示获取失败
        boolean tryAcquire = limiter.tryAcquire(1);
        if (!tryAcquire) {
            throw new RuntimeException("接口限流: 1000ms内最多100次请求");
        }
        // 执行业务逻辑
        System.out.println("执行业务逻辑");
    }
}