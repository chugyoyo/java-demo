package com.chugyoyo.base.javabase.variable;

/**
 * 基本数据类型测试
 */
public class BasicDataTypeTest {

    private static void test1() {
        Integer a;
//        System.out.println(a); // 编译不通过

        Integer b = 100;  // 底层 valueOf
        Integer c = 50 + 50; // 底层 valueOf
        System.out.println(b == c); // true

        // 缓存机制 + 装拆箱，等于号判断不稳定
        Integer i1 = 40;
        Integer i2 = new Integer(40);
        System.out.println(i1==i2); // false

        // 自动装箱和拆箱
        Integer num = 10;  // 自动装箱
        int n = num;       // 自动拆箱

        // 尽量避免不必要的拆装箱操作。
        Long sum = 0L;
        for (long i = 0; i < 1000000; i++) {
            sum += i; // 等价于 sum = Long.valueOf(sum.longValue() + i);
        }

        // 浮点数精度丢失
        float f1 = 2.0f - 1.9f;
        float f2 = 1.8f - 1.7f;
        System.out.printf("%.9f\n",f1);// 0.100000024
        System.out.println(f2);// 0.099999905
        System.out.println(f1 == f2);// false

        // long 溢出
        long l = Long.MAX_VALUE;
        System.out.println(l + 1); // -9223372036854775808
        System.out.println(l + 1 == Long.MIN_VALUE); // true
    }

    public static void main(String[] args) {

        test1();
    }
}
