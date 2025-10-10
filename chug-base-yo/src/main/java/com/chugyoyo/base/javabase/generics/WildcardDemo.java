package com.chugyoyo.base.javabase.generics;

import java.util.ArrayList;
import java.util.List;

public class WildcardDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 通配符演示 ===\n");

        // 2.1 无界通配符 ?
        System.out.println("1. 无界通配符 ?");
        List<?> unboundedList = new ArrayList<String>();
        unboundedList.add(null);  // 只能添加 null
        // unboundedList.add("Hello");  // 编译错误
        
        Object obj = unboundedList.get(0);
        System.out.println("无界通配符列表: " + unboundedList);
        System.out.println("只能读取为 Object 类型\n");

        // 2.2 上界通配符 ? extends T（只能读，不能写）
        System.out.println("2. 上界通配符 ? extends Number");
        List<? extends Number> upperBoundedList = new ArrayList<Integer>();
        List<Integer> integers = new ArrayList<>();
        integers.add(10);
        integers.add(20);
        upperBoundedList = integers;
        
        // 只能读取，不能写入（除了 null）
        Number num = upperBoundedList.get(0);
        System.out.println("读取: " + num);
//         upperBoundedList.add(30);  // 编译错误
        // upperBoundedList.add(new Integer(30));  // 编译错误
        System.out.println("上界通配符列表: " + upperBoundedList);
        System.out.println("特点：只能读取为 Number 或其父类，不能写入\n");

        // 2.3 下界通配符 ? super T（能写入 T 及其子类）
        System.out.println("3. 下界通配符 ? super Integer");
        List<? super Integer> lowerBoundedList = new ArrayList<Number>();
        
        // 可以写入 Integer 及其子类
        lowerBoundedList.add(10);
        lowerBoundedList.add(new Integer(20));
//         lowerBoundedList.add(new Object());  // 编译错误
        
        // 读取时只能作为 Object 类型
        Object superObj = lowerBoundedList.get(0);
        System.out.println("读取: " + superObj);
        System.out.println("下界通配符列表: " + lowerBoundedList);
        System.out.println("特点：可以写入 Integer 及其子类，读取时为 Object 类型\n");

        // 实际应用示例
        System.out.println("=== 实际应用示例 ===");
        
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);
        
        List<Double> doubleList = new ArrayList<>();
        doubleList.add(1.1);
        doubleList.add(2.2);
        doubleList.add(3.3);
        
        List<Number> numberList = new ArrayList<>();
        numberList.add(10);
        numberList.add(20);
        numberList.add(30);
        
        System.out.println("整数列表: " + intList);
        System.out.println("浮点数列表: " + doubleList);
        System.out.println("数字列表: " + numberList);
        
        // 使用上界通配符读取数字
        printNumbers(intList);
        printNumbers(doubleList);
        printNumbers(numberList);
        
        // 使用下界通配符添加数字
        addNumbers(numberList);
        System.out.println("添加后的数字列表: " + numberList);
    }
    
    // 上界通配符方法 - 只能读取
    public static void printNumbers(List<? extends Number> list) {
        System.out.print("打印数字: ");
        for (Number num : list) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    // 下界通配符方法 - 可以写入
    public static void addNumbers(List<? super Integer> list) {
        list.add(100);
        list.add(200);
    }
}