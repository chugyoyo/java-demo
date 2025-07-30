package com.chugyoyo.tool.javabase.proxy;

import lombok.AllArgsConstructor;

public class StaticProxyTest {
    public static void main(String[] args) {
        // 使用
        UserService proxy = new UserServiceProxy(new UserServiceImpl());
        proxy.save();
    }
}

// 3. 静态代理类
@AllArgsConstructor
class UserServiceProxy implements UserService {

    private UserService target;  // 持有真实对象

    @Override
    public void save() {
        System.out.println("before method save()");
        target.save();  // 调用真实对象方法
        System.out.println("before method save()");
    }
}