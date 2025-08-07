package com.chugyoyo.base.javabase.jmm.reorder;

import java.util.concurrent.locks.ReentrantLock;

public class NoVolatileSingleton {

    private static NoVolatileSingleton instance;

    private static ReentrantLock lock = new ReentrantLock();

    private Boolean init = false;

    private String field1;

    private String field2;

    private String field3;

    public NoVolatileSingleton() {
        field1 = "field1";
        field2 = "field2";
        field3 = "field3";
        init = true;
    }

    public boolean isAllInit () {
        return init && field1 != null && field2 != null && field3 != null;
    }

    public static NoVolatileSingleton getInstance() {
        if (instance == null) {
            synchronized (NoVolatileSingleton.class) {
                if (instance == null) {
                    instance = new NoVolatileSingleton();
                }
            }
        }
        return instance;
    }

    // 指令重排序与 synchronized 没有关系
    public static NoVolatileSingleton getInstanceByLock() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new NoVolatileSingleton();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }
}
