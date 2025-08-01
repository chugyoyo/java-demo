package com.chugyoyo.base.javabase.lifecycle.init;

public class ClassInitOrderTest {

    // 第一个执行
    static {
        System.out.println("静态块1");
        value = 1; // 赋值无效
    }

    // 第二个执行
    public static int value = 2;

    // 第三个执行
    static {
        System.out.println(value); // 输出 2
        value = 3;
        System.out.println("静态块3");
        System.out.println(value); // 输出 3
    }

    public int key = 4;

    {
        System.out.println("key=" + key); // 这里将不会执行
    }

    public static void main(String[] args) {
    }
}
