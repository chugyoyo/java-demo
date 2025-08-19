package com.chugyoyo.base.javabase.operator;

public class OperatorDemo {

    public static void main(String[] args) {
        int a = 10, b = 3;

        // 算术运算符
        System.out.println("a + b = " + (a + b));  // 13
        System.out.println("a - b = " + (a - b));  // 7
        System.out.println("a * b = " + (a * b));  // 30
        System.out.println("a / b = " + (a / b));  // 3
        System.out.println("a % b = " + (a % b));  // 1

        // 自增自减
        int c = 5;
        System.out.println("c++ = " + (c++)); // 5
        System.out.println("++c = " + (++c)); // 7

        // 关系运算符
        System.out.println("a > b ? " + (a > b));  // true
        System.out.println("a == b ? " + (a == b));// false

        // 逻辑运算符
        boolean x = true, y = false;
        System.out.println("x && y = " + (x && y)); // false
        System.out.println("x || y = " + (x || y)); // true
        System.out.println("!x = " + (!x));         // false

        // 位运算符
        System.out.println("a & b = " + (a & b));   // 2
        System.out.println("a | b = " + (a | b));   // 11
        System.out.println("a ^ b = " + (a ^ b));   // 9
        System.out.println("~a = " + (~a));         // -11
        System.out.println("a << 1 = " + (a << 1)); // 20

        // 赋值运算符
        int d = 5;
        d += 3; // 相当于 d = d + 3
        System.out.println("d = " + d); // 8

        // 三元运算符
        int max = (a > b) ? a : b;
        System.out.println("max = " + max);

        // instanceof
        String str = "Hello";
        System.out.println("str instanceof String ? " + (str instanceof String));
    }
}

