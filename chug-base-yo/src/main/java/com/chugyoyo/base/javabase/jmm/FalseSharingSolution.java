package com.chugyoyo.base.javabase.jmm;

// 使用填充解决伪共享
public class FalseSharingSolution {
    static class ValueHolder {
        volatile long value;
        long p1, p2, p3, p4, p5, p6; // 填充至64字节
    }
}
