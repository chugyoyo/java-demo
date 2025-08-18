package com.chugyoyo.db.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test-distributed-lock")
public class TestDistributedLockController {

    @DistributedLock(key = "chugyoyo:tryLock", waitTime = 3)
    @RequestMapping("/try-lock")
    public String tryLock() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "try-lock";
    }
}
