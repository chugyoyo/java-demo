package com.chugyoyo.web.distribute.monitor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class MonitorAspect {

    @Around("@annotation(monitor)")
    public Object recordExecutionTime(ProceedingJoinPoint joinPoint, Monitor monitor) throws Throwable {
        long start = System.currentTimeMillis();
        boolean success = true;
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            success = false;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            String methodName = joinPoint.getSignature().toShortString();
            String logLine = String.format("%s | %dms | success=%s", methodName, duration, success);
            log.info(logLine);
        }
    }
}

