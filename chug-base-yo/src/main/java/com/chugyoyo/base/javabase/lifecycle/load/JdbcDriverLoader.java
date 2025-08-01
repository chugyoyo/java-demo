package com.chugyoyo.base.javabase.lifecycle.load;

import java.sql.Driver;
import java.util.ServiceLoader;

public class JdbcDriverLoader {

    public static void classic () {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void spiLoad () {
        try {
            Thread.currentThread().setContextClassLoader(
                    new CustomClassLoader(
                            CustomClassLoader.class.getResource("/class/").getPath()
                    )
            );
            ServiceLoader<Driver> driverServiceLoader = ServiceLoader.load(Driver.class);
            for (Driver driver : driverServiceLoader) {
                System.out.println(driver);
            }
            // 现代方式（JDBC 4.0+）
//            Connection conn = DriverManager.getConnection(url); // 自动发现驱动
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        spiLoad();
    }
}
