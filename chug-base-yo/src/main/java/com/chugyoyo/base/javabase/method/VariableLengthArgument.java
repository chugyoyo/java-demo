package com.chugyoyo.base.javabase.method;


public class VariableLengthArgument {

    //  编译后：（底层是数组）
    //        String[] var1 = args;
    //        int var2 = args.length;
    //
    //        for(int var3 = 0; var3 < var2; ++var3) {
    //            String s = var1[var3];
    //            System.out.println(s);
    //        }
    public static void printVariable(String... args) {
        for (String s : args) {
            System.out.println(s);
        }
    }

    public static void printVariable(String arg1, String arg2) {
        System.out.println(arg1 + arg2);
    }

    public static void main(String[] args) {
        printVariable("a", "b"); // 优先匹配固定参数的方法，因为固定参数的方法匹配度更高。
        printVariable("a", "b", "c", "d");
    }
}