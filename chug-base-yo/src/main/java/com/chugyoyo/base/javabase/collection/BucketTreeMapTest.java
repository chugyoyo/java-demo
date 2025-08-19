package com.chugyoyo.base.javabase.collection;

import java.util.*;

/**
 * 测试 1M 数据量下，16 个桶的红黑树 VS 红黑树 的效率
 */
public class BucketTreeMapTest {

    public static final int N = 1_000_000;

    public static TreeMap<Integer, Integer>[] bucketTreeMap;

    public static TreeMap<Integer, Integer> treeMap;

    static {
        bucketTreeMap = new TreeMap[16];
        for (int i = 0; i < bucketTreeMap.length; i++) {
            bucketTreeMap[i] = new TreeMap<Integer, Integer>();
        }
        treeMap = new TreeMap<>();
    }

    public static void main(String[] args) {
        testTreeMap("treeMap", treeMap);
        testBucketTreeMap("bucketTreeMap", bucketTreeMap);
    }

    private static void testTreeMap(String name, Map<Integer, Integer> map) {
        Random rand = new Random(42);

        // 插入测试
        long start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            int key = rand.nextInt(N * 10);
            map.put(key, i);
        }
        long insertTime = System.nanoTime() - start;

        // 查询测试（命中 + 未命中混合）
        start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            int key = rand.nextInt(N * 10);
            map.get(key);
        }
        long searchTime = System.nanoTime() - start;

        System.out.printf("%s -> 插入: %.3f s, 查询: %.3f s, 总: %.3f s%n",
                name,
                insertTime / 1e9,
                searchTime / 1e9,
                (insertTime + searchTime) / 1e9);
    }

    private static void testBucketTreeMap(String name, TreeMap<Integer, Integer>[] bucketTreeMap) {
        Random rand = new Random(42);

        // 插入测试
        long start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            int key = rand.nextInt(N * 10);
            int index = key % 16;
            TreeMap<Integer, Integer> treeMap = bucketTreeMap[index];
            treeMap.put(key, i);
        }
        long insertTime = System.nanoTime() - start;

        // 查询测试（命中 + 未命中混合）
        start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            int key = rand.nextInt(N * 10);
            int index = key % 16;
            TreeMap<Integer, Integer> treeMap = bucketTreeMap[index];
            treeMap.get(key);
        }
        long searchTime = System.nanoTime() - start;

        System.out.printf("%s -> 插入: %.3f s, 查询: %.3f s, 总: %.3f s%n",
                name,
                insertTime / 1e9,
                searchTime / 1e9,
                (insertTime + searchTime) / 1e9);
    }
}