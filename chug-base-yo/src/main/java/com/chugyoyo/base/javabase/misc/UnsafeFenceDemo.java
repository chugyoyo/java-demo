package com.chugyoyo.base.javabase.misc;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeFenceDemo {
    static Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void main(String[] args) throws Exception {
        for (; ; ) {
            x = y = a = b = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
//                unsafe.storeFence(); // 保证写a的可见性
                x = b;
            });
            Thread t2 = new Thread(() -> {
                b = 1;
//                unsafe.storeFence();
                y = a;
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            if (x == 0 && y == 0) {
                System.out.println("Reordering detected!");
                break;
            }
        }
    }
}

