package com.chugyoyo.web.breaker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SimpleCircuitBreaker {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<?> future = executor.submit(() -> {
            // 模拟耗时操作
//            Thread.sleep(10_000);
            return "Done";
        });

        try {
            Object result = future.get(5, TimeUnit.SECONDS);
            System.out.println(result + " done!");
        } catch (Exception e) {
            future.cancel(true); // 中断任务
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                return;
            }
            System.out.println("Timeout");
            return;
        }
    }
}
