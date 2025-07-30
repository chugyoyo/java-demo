package com.chugyoyo.tool.javabase.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyTest {
    // 演示 CGLIB 代理
    public static void main(String[] args) {
        UserService proxy = (UserService) CglibProxyFactory.createProxy(UserServiceImpl.class);
        proxy.save();
    }
}

class CglibProxyFactory {

    public static Object createProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(clazz.getClassLoader()); // 指定类加载器
        enhancer.setSuperclass(clazz);  // 设置父类（真实对象）
        enhancer.setCallback(new LogInterceptor());  // 设置拦截器
        return enhancer.create();
    }
}

class LogInterceptor implements MethodInterceptor {
    /**
     * @param obj    被代理的对象（需要增强的对象）
     * @param method 被拦截的方法（需要增强的方法）
     * @param args   方法入参
     * @param proxy  用于调用原始方法
     * @return 方法返回值
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("before method - " + method.getName());
        return proxy.invokeSuper(obj, args);  // 调用父类（真实对象）方法
    }
}