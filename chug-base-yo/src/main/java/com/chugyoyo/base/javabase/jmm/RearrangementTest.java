package com.chugyoyo.base.javabase.jmm;

/**
 * 重排序测试
 * 看 JDK 版本，JDK 1.6 及以下版本，指令重排序会导致对象未初始化完成时，其他线程获取到未初始化的对象
 * JDK 1.7 及以上版本，指令重排序不会导致对象未初始化完成时，其他线程获取到未初始化的对象
 */
public class RearrangementTest
{
    private int i;

    public RearrangementTest()
    {
        i = 1;
    }

    public static void main(String[] args)
    {
        RearrangementTest t = new RearrangementTest();
    }
}