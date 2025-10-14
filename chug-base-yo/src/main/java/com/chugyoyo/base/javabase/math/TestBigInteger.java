package com.chugyoyo.base.javabase.math;

import java.math.BigInteger;
import java.util.Arrays;

public class TestBigInteger {
    public static void main(String[] args) {
        BigInteger bi = new BigInteger("123456789");
        System.out.println("十进制: " + bi);
        System.out.println("字节数组: " + Arrays.toString(bi.toByteArray()));
        System.out.println("转16进制: " + bi.toString(16));
    }
}

