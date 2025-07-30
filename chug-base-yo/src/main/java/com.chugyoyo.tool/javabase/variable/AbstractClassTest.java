package com.chugyoyo.tool.javabase.variable;

public abstract class AbstractClassTest {

    protected abstract void test1(); // 子类去实现
    protected abstract void test2(); // 子类去实现

    // 模版模式：定义执行顺序
    public void test() {
        try {
            test1();
            test2();
        } catch (Exception e) {
            return;
        }
    }

    private void test3() { // 这里用不到
        System.out.println("test3");
    }
}
