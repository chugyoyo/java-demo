package com.chugyoyo.base.javabase.jmm;

/**
 * 初始化和 set 的指令重排序测试
 * 看操作系统、JVM 版本等因素
 */
public class InitSetTest
{
    private int i;

    public InitSetTest()
    {
        i = 1;
    }

    // 字节码仅供参考，JDK8、Mac 实测，不同 JDK、操作系统 版本可能不一样
    //       stack=2, locals=2, args_size=1
    //         0: new           #3                  // class com/chugyoyo/base/javabase/jmm/RearrangementTest
    //         3: dup
    //         4: invokespecial #4                  // Method "<init>":()V
    //         7: astore_1
    //         8: return
    public static void main(String[] args)
    {
        InitSetTest t = new InitSetTest();
    }
}