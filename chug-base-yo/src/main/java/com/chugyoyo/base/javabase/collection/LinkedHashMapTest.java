package com.chugyoyo.base.javabase.collection;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapTest {

    public static void testLinkedHashMap() {
        int N = 1_000_000;

        Map<Integer, Integer> linkedMap = new LinkedHashMap<>(16, 0.75f, false);

        long time1 = System.currentTimeMillis();

        // 插入测试
        for (int i = 0; i < N; i++) {
            linkedMap.put(i, i);
        }

        long time2 = System.currentTimeMillis();

        // 访问测试
        for (int i = 0; i < N; i++) {
            linkedMap.get(i);
        }

        long time3 = System.currentTimeMillis();

        System.out.println("插入需要ms:" + (time2 - time1) + "ms");
        System.out.println("访问需要ms:" + (time3 - time2) + "ms");

    }

    private static void testHashMap() {

        int N = 1_000_000;

        Map<Integer, Integer> hashMap = new HashMap<>(16, 0.75f);

        long time1 = System.currentTimeMillis();

        // 插入测试
        for (int i = 0; i < N; i++) {
            hashMap.put(i, i);
        }

        long time2 = System.currentTimeMillis();

        // 访问测试
        for (int i = 0; i < N; i++) {
            hashMap.get(i);
        }

        long time3 = System.currentTimeMillis();

        System.out.println("插入需要ms:" + (time2 - time1) + "ms");
        System.out.println("访问需要ms:" + (time3 - time2) + "ms");

    }

    public static void main(String[] args) {
        System.out.println("testLinkedHashMap");
        testLinkedHashMap();
        System.out.println("testHashMap");
        testHashMap();
        System.out.println("testLinkedHashMap");
        testLinkedHashMap();
        System.out.println("testHashMap");
        testHashMap();
        System.out.println("testLinkedHashMap");
        testLinkedHashMap();
        System.out.println("testHashMap");
        testHashMap();
        System.out.println("testLinkedHashMap");
        testLinkedHashMap();
        System.out.println("testHashMap");
        testHashMap();
        System.out.println("testLinkedHashMap");
        testLinkedHashMap();
        System.out.println("testHashMap");
        testHashMap();
        System.out.println("testLinkedHashMap");
        testLinkedHashMap();
        System.out.println("testHashMap");
        testHashMap();
    }
}
