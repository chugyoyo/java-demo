package com.chugyoyo.base.javabase.operator;

public class OperatorPriorityDemo {
    public static void main(String[] args) {
        int a = 2, b = 3, c = 4;

        // * 比 + 优先级高
        System.out.println("a + b * c = " + (a + b * c)); // 2 + (3*4) = 14

        // 括号改变优先级
        System.out.println("(a + b) * c = " + ((a + b) * c)); // (2+3)*4 = 20

        // 关系运算符 vs 逻辑运算符
        System.out.println("a < b && b < c = " + (a < b && b < c)); // true && true = true

        // 三元运算符
        int max = (a > b) ? a : b;
        System.out.println("max = " + max); // 3

        // 赋值运算符（右结合）
        int x, y, z;
        x = y = z = 10; // 从右到左
        System.out.println("x=" + x + ", y=" + y + ", z=" + z);
    }
}
