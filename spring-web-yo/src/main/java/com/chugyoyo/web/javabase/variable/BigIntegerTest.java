package com.chugyoyo.web.javabase.variable;

import java.math.BigInteger;

public class BigIntegerTest {

    public static void main(String[] args) {
        BigInteger big1 = new BigInteger("123456789012345678901234567890");
        BigInteger big2 = new BigInteger("987654321098765432109876543210");

        // 运算
        BigInteger sum = big1.add(big2);
        BigInteger product = big1.multiply(big2);

        // 进制转换
        String hex = big1.toString(16); // 转为16进制字符串

        System.out.println(sum);
        System.out.println(product);
        System.out.println(hex);
    }
}
