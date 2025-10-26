package com.chugyoyo.db.trace;

import org.slf4j.MDC;

public class TraceContext {
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public static void set(String traceId) {
        TRACE_ID.set(traceId);
        // 同步日志框架 MDC
        MDC.put("traceId", traceId);
    }

    public static String get() {
        return TRACE_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
        // 同步日志框架 MDC
        MDC.remove("traceId");
    }

    public static String initIfAbsent() {
        String traceId = TRACE_ID.get();
        if (traceId == null) {
            traceId = java.util.UUID.randomUUID().toString().replace("-", "");
            TRACE_ID.set(traceId);
        }
        return traceId;
    }
}