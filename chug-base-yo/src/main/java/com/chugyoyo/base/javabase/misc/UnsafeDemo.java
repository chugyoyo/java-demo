package com.chugyoyo.base.javabase.misc;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class UnsafeDemo {

    // 获取 Unsafe 实例
    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Cannot access Unsafe", e);
        }
    }

    static class User {
        private String name = "default";
        private int age = 18;

        public User() {
            System.out.println("User() 构造函数被调用");
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        Unsafe unsafe = getUnsafe();

        System.out.println("=== 1️⃣ 直接操作对象字段 ===");
        User user = new User();
        System.out.println("原始: " + user);

        // 获取字段偏移量
        Field nameField = User.class.getDeclaredField("name");
        Field ageField = User.class.getDeclaredField("age");
        long nameOffset = unsafe.objectFieldOffset(nameField);
        long ageOffset = unsafe.objectFieldOffset(ageField);

        // 直接修改字段
        unsafe.putObject(user, nameOffset, "Alice");
        unsafe.putInt(user, ageOffset, 30);
        System.out.println("修改后: " + user);

        // CAS 原子更新 age 字段
        boolean swapped = unsafe.compareAndSwapInt(user, ageOffset, 30, 35);
        System.out.println("CAS 结果: " + swapped + "，更新后: " + user);

        System.out.println("\n=== 2️⃣ 绕过构造函数实例化 ===");
        User u2 = (User) unsafe.allocateInstance(User.class);
        System.out.println("绕过构造函数实例化: " + u2);
        unsafe.putObject(u2, nameOffset, "Bob");
        unsafe.putInt(u2, ageOffset, 25);
        System.out.println("修改后: " + u2);

        System.out.println("\n=== 3️⃣ 直接分配堆外内存 ===");
        long memoryAddress = unsafe.allocateMemory(8); // 分配 8 字节
        unsafe.putLong(memoryAddress, 123456789L);
        long value = unsafe.getLong(memoryAddress);
        System.out.println("堆外内存读取值: " + value);
        unsafe.freeMemory(memoryAddress);
        System.out.println("堆外内存释放完成");

        System.out.println("\n=== 4️⃣ 模拟内存屏障 ===");
        unsafe.storeFence(); // 写屏障
        unsafe.loadFence();  // 读屏障
        unsafe.fullFence();  // 全屏障
        System.out.println("内存屏障执行完成");
    }
}

