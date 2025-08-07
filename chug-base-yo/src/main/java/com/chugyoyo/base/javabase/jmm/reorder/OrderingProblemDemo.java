package com.chugyoyo.base.javabase.jmm.reorder;

public class OrderingProblemDemo {
    private static int x = 0;
    private static int y = 0;
    private static int a = 0;
    private static int b = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 100_000; i++) {
            // 重置变量
            x = 0; y = 0; a = 0; b = 0;

            Thread threadA = new Thread(() -> {
                a = 1;  // 操作A
                x = b;  // 操作B
            });

            Thread threadB = new Thread(() -> {
                b = 1;  // 操作C
                y = a;  // 操作D
            });

            threadA.start();
            threadB.start();

            threadA.join();
            threadB.join();

            // 如果指令按程序顺序执行，不可能出现 x==0 且 y==0
            if (x == 0 && y == 0) {
                System.out.printf("⚠️ 指令重排序发生! (第 %d 次迭代)%n", i + 1);
                System.out.println("x=" + x + ", y=" + y);
                return;
            }
        }
        System.out.println("✅ 未检测到指令重排序");
    }
}
