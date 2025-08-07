package com.chugyoyo.base.javabase.jmm;

public class VolatileExample {

    public
//    volatile
    int value;

    public void set(int v) {
        // StoreStore 屏障
        value = v; // volatile 写
        // StoreLoad 屏障
    }

    public int get() {
        // LoadLoad 屏障
        int temp = value; // volatile 读
        // LoadStore 屏障
        return temp;
    }
}
