package com.chugyoyo.base.javabase.jmm.visibility;

import lombok.Data;

@Data
public class VolatileExample {

    private
//    volatile
    boolean running = true;

    public static boolean isRunning2 = true;
}
