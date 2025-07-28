package com.chugyoyo.web.javabase.lifecycle.load;

import lombok.SneakyThrows;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class FileClassLoadAndUnloadTest {

    @SneakyThrows
    public static void main(String[] args) {

        String resourcePath = "/class/";
        String className = "FileClassLoadDemo";
        String invokeMethodName = "sayHello";

        // 从 resources 读取类文件
        URL resourceUrl = FileClassLoadAndUnloadTest.class.getResource(resourcePath);

        // JDK 编译，防止版本问题
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assert resourceUrl != null;
        int result1 = compiler.run(null, null, null,
                // javac ${resourceUrl}/xx.java -d ${resourceUrl}
                resourceUrl.getPath() + "/" + className + ".java", "-d", resourceUrl.getPath());
        assert result1 != 0;

        // 用户自定义类加载器
        URLClassLoader classLoader = new URLClassLoader(new URL[]{resourceUrl}) {
            @Override
            protected void finalize() throws Throwable {
                System.out.println(this + " user define class loader is finalized");
                super.finalize();
            }
        };

        Class<?> clazz = classLoader.loadClass(className);
        Object instance = clazz.newInstance();

        Method method = clazz.getMethod(invokeMethodName);
        String result = (String) method.invoke(instance);
        System.out.println("invoke result:" + result);

        WeakReference<Class<?>> classRef = new WeakReference<>(clazz);
        WeakReference<Object> instanceRef = new WeakReference<>(instance);
        WeakReference<ClassLoader> loaderRef = new WeakReference<>(classLoader);

//        clazz = null;
//        instance = null;

//        if (classLoader instanceof Closeable) {
//            ((Closeable) classLoader).close();
//        }
//        classLoader = null;

        // 用大对象触发垃圾回收
        for (int i = 0; i < 1000000; i++) {
            new Object();
        }
        forceGc(); // 3 次

        System.out.println("\n清除强引用后:");
        System.out.println("  Class引用: " + classRef.get());
        System.out.println("  实例引用: " + instanceRef.get());
        System.out.println("  加载器引用: " + loaderRef.get());
    }

    private static void forceGc() {
        System.out.println("\n请求垃圾回收...");
        for (int i = 0; i < 5; i++) {
            System.gc();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
