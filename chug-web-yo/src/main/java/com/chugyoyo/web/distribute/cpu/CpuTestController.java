package com.chugyoyo.web.distribute.cpu;

import com.chugyoyo.web.distribute.breaker.TimeoutCircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/cpu-test")
@RestController
@Slf4j
public class CpuTestController {

    // 死循环，就算客户端网络超时了，这边 worker 线程也不会断掉，CPU 持续运转
    @TimeoutCircuitBreaker(timeout = 5000)
    @GetMapping("/dead-loop")
    public ResponseEntity<String> deadLoop(@RequestParam(value = "isDeadLoop", required = true) Boolean isDeadLoop) {
        while (isDeadLoop) {

        }
        return ResponseEntity.ok("dead-loop finish");
    }
}
