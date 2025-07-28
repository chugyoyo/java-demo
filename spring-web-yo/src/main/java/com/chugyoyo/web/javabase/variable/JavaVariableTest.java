package com.chugyoyo.web.javabase.variable;

public class JavaVariableTest {
    char c = '\u0000';
    String s = "Hello\u0000";

    Thread t;

    public static void main(String[] args) {
        final JavaVariableTest javaVariableTest = new JavaVariableTest();
//        javaVariable = new JavaVariable();
        System.out.println(javaVariableTest.c);
        System.out.println(javaVariableTest.s);
    }
}
