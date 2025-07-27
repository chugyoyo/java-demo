package com.chugyoyo.web.variable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// 深、浅、引用拷贝
public class CopyTest {

    private static class A implements Cloneable {

        @AllArgsConstructor
        private static class AInner implements Cloneable {
            @Setter @Getter
            int inner;

            @Override
            public AInner clone() {
                try {
                    AInner clone = (AInner) super.clone();
                    // TODO: copy mutable state here, so the clone can't change the internals of the original
                    return clone;
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError();
                }
            }
        }

        int a;
        @Setter @Getter
        AInner aInner = new AInner(10);

        @Override
        protected A clone() {
            // 默认浅拷贝
            try {
                return (A) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        // 深拷贝
        protected A deepClone() {
            try {
                A clone = (A) super.clone(); // 浅拷贝
                clone.setAInner(this.aInner.clone()); // 浅拷贝
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        A a1 = new A();

        // 浅拷贝
        A a2 = a1.clone();
        System.out.println(a1.aInner == a2.aInner); // true

        // 深拷贝
        A a3 = a1.deepClone();
        System.out.println(a1.aInner == a3.aInner); // false

        // 引用拷贝
        A a4 = a1;
        System.out.println(a1 == a4); // true
    }
}
