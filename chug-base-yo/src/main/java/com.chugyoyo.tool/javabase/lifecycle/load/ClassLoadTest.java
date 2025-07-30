package com.chugyoyo.tool.javabase.lifecycle.load;

public class ClassLoadTest {

    private static class A {
        public void test() {
            System.out.println("A test()");
        }
    }

    // 数组的类加载器，是由数组元素的类型的类加载器加载的，二者相同
    public static void test() {
        System.out.println(A.class.getClassLoader()); // sun.misc.Launcher$AppClassLoader@xxxx
        System.out.println(A[].class.getClassLoader()); // sun.misc.Launcher$AppClassLoader@xxxx
    }

    // 启动类加载器，打印出来是 null。
    public static void test2() {
        System.out.println(Object.class.getClassLoader()); // null
    }

    public static void test3() {
        System.out.println(ClassLoadTest.class.getClassLoader());
    }

    // 打印类加载器链，启动类加载器打印出来是 null。
    public static void test4() {
        ClassLoader classLoader = ClassLoadTest.class.getClassLoader();

        StringBuilder split = new StringBuilder("|--");
        boolean needContinue = true;
        while (needContinue) {
            System.out.println(split.toString() + classLoader);
            if (classLoader == null) {
                needContinue = false;
            } else {
                classLoader = classLoader.getParent();
                split.insert(0, "\t");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("test()");
        test();
        System.out.println("test2()");
        test2();
        System.out.println("test3()");
        test3();
        System.out.println("test4()");
        test4();
    }
}
