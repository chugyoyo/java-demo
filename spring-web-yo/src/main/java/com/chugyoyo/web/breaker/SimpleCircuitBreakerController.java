package com.chugyoyo.web.breaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RequestMapping("/circuitBreaker")
@RestController
public class SimpleCircuitBreakerController {

    @Autowired
    private PaymentService paymentService;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/process")
    public ResponseEntity<?> process() {
        Future<?> future = executor.submit(() -> {
            // 模拟耗时操作
            Thread.sleep(10_000);
            System.out.println("耗时操作完成");
            return "耗时操作完成";
        });

        try {
            Object result = future.get(5, TimeUnit.SECONDS);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            future.cancel(true); // 中断任务
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Timeout");
        }
    }
}
