package com.chugyoyo.web.annotation.runtime;

import java.lang.reflect.Method;

public class PrintLogTest {

    @PrintLog(level = PrintLog.Level.INFO)
    private static class TestClass {

        @PrintLog(level = PrintLog.Level.INFO)
        public String testMethod(String param) {
            return "return param = " + param;
        }
    }


    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        String result = testClass.testMethod("hello");
        System.out.println(result);
        printLogTest(testClass);
    }

    // 这里可以改为 AOP 逻辑
    public static void printLogTest(Object obj) {
        Class<?> clazz = obj.getClass();

        // 类级别注解
        if (clazz.isAnnotationPresent(PrintLog.class)) {
            PrintLog anno = clazz.getAnnotation(PrintLog.class);
            System.out.println("Class level: " + anno.level());
        }

        // 方法级别注解
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PrintLog.class)) {
                PrintLog anno = method.getAnnotation(PrintLog.class);
                System.out.println("Method " + method.getName() +
                        " level: " + anno.level());
            }
        }
    }
}
