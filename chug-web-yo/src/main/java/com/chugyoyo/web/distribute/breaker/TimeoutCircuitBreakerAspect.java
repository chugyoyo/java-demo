package com.chugyoyo.web.distribute.breaker;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 超时断路器 AOP 切面
 *
 * @author chugyoyo
 * @since 2025/8/1
 */
@Aspect
@Component
public class TimeoutCircuitBreakerAspect {

    // 使用固定线程池（实际生产环境需配置合适的参数）
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Around("@annotation(timeoutCircuitBreaker)")
    public Object executeWithTimeout(ProceedingJoinPoint joinPoint,
                                     TimeoutCircuitBreaker timeoutCircuitBreaker)
            throws Throwable {
        long timeout = timeoutCircuitBreaker.timeout();
        String fallbackMethod = timeoutCircuitBreaker.fallback();

        // 提交任务到线程池
        Future<Object> future = executor.submit(() -> {
            try {
                return joinPoint.proceed(); // 执行目标方法
            } catch (Throwable e) {
                throw new ExecutionException(e);
            }
        });

        try {
            // 设置超时等待
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // 中断方法执行
            if (!fallbackMethod.isEmpty()) {
                // 执行降级方法
                return callFallbackMethod(joinPoint, fallbackMethod);
            }
            throw new RuntimeException("Method execution timed out");
        } catch (ExecutionException e) {
            throw new RuntimeException("Execution failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted");
        }
    }

    private Object callFallbackMethod(ProceedingJoinPoint joinPoint, String fallbackMethod)
            throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object target = joinPoint.getTarget();

        // 查找同名同参数的降级方法
        return target.getClass()
                .getMethod(fallbackMethod, signature.getParameterTypes())
                .invoke(target, joinPoint.getArgs());
    }
}