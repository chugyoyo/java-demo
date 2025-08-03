package com.chugyoyo.db.controller;

import com.chugyoyo.db.mapper.ChugUserMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Slf4j
@RestController
@RequestMapping("/chug-user-test")
public class ChugUserTestController {

    // 静态预热
    private static final Cache<Long, String> userIdUserNameLocalCache = Caffeine.newBuilder()
            // 使用随机过期时间
            .expireAfterWrite(5000 + ThreadLocalRandom.current().nextInt(5000), TimeUnit.MILLISECONDS)
            //最大容量 10000 个，超过会自动清理空间
            .maximumSize(10_000)
            .removalListener(((key, value, cause) -> {
                switch (cause) {
                    case EXPLICIT: // 手动无效
                        log.debug("手动删除: {}", key);
                        break;
                    case REPLACED: // 值被替换
                        log.debug("值替换: {}", key);
                        break;
                    case EXPIRED: // 过期驱逐
                        log.debug("过期删除: {}", key);
                        break;
                    case SIZE: // 大小驱逐
                        log.debug("空间不足删除: {}", key);
                        break;
                    case COLLECTED: // 引用回收
                        log.debug("GC回收: {}", key);
                        break;
                }
            }))
            .recordStats() // 记录
            .build();

    // 定期打印统计
    @Scheduled(fixedRate = 60_000)
    public void logCacheStats() {
        CacheStats stats = userIdUserNameLocalCache.stats();
        log.debug("缓存命中率: {}%, 加载次数: {}", stats.hitRate() * 100, stats.loadCount());
    }

    @Autowired
    private ChugUserMapper chugUserMapper;

    // 测试 Mybatis 方法重载
    @RequestMapping("/get-user-name-test-1")
    public String getUserName(@RequestParam(value = "id", required = true) Long id,
                              @RequestParam(value = "status", required = false) Integer status) {
        if (status == null) {
            return chugUserMapper.getUserName(id);
        }
        return chugUserMapper.getUserName(id, status);
    }

    // 测试本地缓存
    @RequestMapping("/get-user-name-test-2")
    public String getUserName(@RequestParam(value = "id", required = true) Long id) {
        String userName = userIdUserNameLocalCache.getIfPresent(id);
        if (userName == null) {
            synchronized (this) {
                userName = userIdUserNameLocalCache.getIfPresent(id);
                if (userName == null) {
                    userName = chugUserMapper.getUserNameById(id);
                    userName = userName == null ? "" : userName; // 空值解穿透，但可能会污染业务
                    userIdUserNameLocalCache.put(id, userName);
                }
                return userName;
            }
        }
        return userName;
    }
}
