package com.chugyoyo.base.javabase.collection;

import java.util.PriorityQueue;

public class PriorityQueueDemo {

    public static void main(String[] args) {
        // 创建最小堆（默认）
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        minHeap.offer(5);
        minHeap.offer(1);
        minHeap.offer(3);
        minHeap.offer(2);

        System.out.println(minHeap.poll()); // 1
        System.out.println(minHeap.poll()); // 2
        System.out.println(minHeap.poll()); // 3
        System.out.println(minHeap.poll()); // 5
    }
}
