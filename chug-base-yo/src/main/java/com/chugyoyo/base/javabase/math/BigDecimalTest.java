package com.chugyoyo.base.javabase.math;

import java.math.BigDecimal;

public class BigDecimalTest {

    public static void main(String[] args) {
        System.out.println(new BigDecimal("1.0").equals(new BigDecimal("1")));    // false
        System.out.println(new BigDecimal("1.0").compareTo(new BigDecimal("1"))); // 0(相等)
    }
}
