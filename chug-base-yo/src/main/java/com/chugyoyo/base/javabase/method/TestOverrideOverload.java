package com.chugyoyo.base.javabase.method;

class Animal {
    protected void speak() {
        System.out.println("Animal speaks");
    }
}

class Dog extends Animal {
    // 重写 (Override) 父类方法
//    @Override // // 用来检查
    public void speak() {
        System.out.println("Dog barks");
    }

    // 重载 (Overload) 方法：参数不同
    public void speak(String sound) {
        System.out.println("Dog says: " + sound);
    }

    // 编译异常，'speak(String)' is already defined
//    public String speak(String sound) {
//        System.out.println("Dog says: " + sound);
//        return "Dog says: " + sound;
//    }

    // 编译异常，'speak(String)' is already defined
//    public void speak(String sound2) {
//    }
}

public class TestOverrideOverload {
    public static void main(String[] args) {
        Animal a = new Animal();
        Animal b = new Dog();
        Dog c = new Dog();

        // 普通 Animal
        a.speak(); // Animal speaks

        // 多态：调用的是 Dog 的重写方法
        b.speak(); // Dog barks

        // 直接用 Dog
        c.speak();          // Dog barks （重写）
        c.speak("Woof!");   // Dog says: Woof! （重载）
    }
}
