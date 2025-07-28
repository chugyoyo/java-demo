package com.chugyoyo.web.javabase.proxy;

import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyTest {

    // 演示 JDK 动态代理
    public static void main(String[] args) {
        UserService realService = new UserServiceImpl();
        UserService proxy = (UserService) JdkProxyFactory.getProxy(realService);
        proxy.save();
    }
}

class JdkProxyFactory {
    public static Object getProxy(Object target) {
        UserService realService = new UserServiceImpl();
        return (UserService) Proxy.newProxyInstance(
                realService.getClass().getClassLoader(), // 指定类加载器，用于加载代理对象。
                realService.getClass().getInterfaces(),  // 指定接口，代理对象会实现这些接口。
                new LogInvocationHandler(realService) // 指定调用 InvocationHandler（实现），用于处理方法调用。
        );
    }
}

// 自定义 InvocationHandler 实现类，用于处理方法调用
@AllArgsConstructor
class LogInvocationHandler implements InvocationHandler {

    private Object target; // 真实对象

    /**
     * @param proxy  动态生成的代理类
     * @param method 当前 method 方法的参数
     * @param args   与代理类对象调用的方法相对应
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before method - " + method.getName());
        return method.invoke(target, args);  // 反射调用真实方法
    }
}
