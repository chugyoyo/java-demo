package com.chugyoyo.base.javabase.lifecycle.load;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomClassLoader extends ClassLoader {

    private final String classPath;

    public CustomClassLoader(String classPath) {
        this.classPath = classPath;
    }

    // 不推荐，破坏 JVM 稳定性
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 1. 对特定包名直接自行加载
        if (name.startsWith("com.chugyoyo.web")) {
            return findClass(name);
        }
        // 2. 其他类仍走双亲委派（底层是双亲委派的模版方法）
        return super.loadClass(name);
    }

    // 必须重写，不然底层默认是抛出 ClassNotFoundException
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        return defineClass(name, classData, 0, classData.length); // 安全校验机制
    }

    private byte[] loadClassData(String className) {
        // 1. 将类名转换为文件路径
        String path = classPath + File.separatorChar +
                className.replace('.', File.separatorChar) + ".class";
        try (InputStream is = new FileInputStream(path);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 2. 读取字节码（此处可添加解密逻辑）
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static void main(String[] args) {
        CustomClassLoader customClassLoader = new CustomClassLoader(
                // 确保 target 目录中有 .class 文件，否则会 java.io.FileNotFoundException: No such file or directory
                CustomClassLoader.class.getResource("/class/").getPath()
        );
        Class<?> fileClassLoadDemo = customClassLoader.loadClass("FileClassLoadDemo");
        assert fileClassLoadDemo != null;
        Object object = fileClassLoadDemo.newInstance();
        System.out.println(object);
    }
}
