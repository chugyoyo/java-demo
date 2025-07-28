package com.chugyoyo.web.javabase.proxy;

// 2. 真实对象
class UserServiceImpl implements UserService {

    public void save() {
        System.out.println("save user");
    }
}
