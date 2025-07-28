package com.chugyoyo.web.javabase.classload;

public class ClassLoadTest {


    private static class A {
        public void test() {
            System.out.println("ClassLoadDemo demo");
        }
    }

    // 数组的类加载器，是由数组元素的类型的类加载器加载的，二者相同
    public static void test() {
        System.out.println(A.class.getClassLoader()); // sun.misc.Launcher$AppClassLoader@xxxx
        System.out.println(A[].class.getClassLoader()); // sun.misc.Launcher$AppClassLoader@xxxx
    }

    public static void main(String[] args) {
        test();
    }
}
