package com.chugyoyo.web.javabase.variable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// 深、浅、引用拷贝
@AllArgsConstructor
public class CopyTest implements Cloneable { // 必须实现 Cloneable 接口，否则抛出 java.lang.CloneNotSupportedException

    int a;
    @Getter @Setter
    CopyInner copyInner; // 对比深、浅拷贝的区别

    @AllArgsConstructor
    private static class CopyInner implements Cloneable{
        @Getter
        int inner;

        @Override
        public CopyInner clone() {
            try {
                CopyInner clone = (CopyInner) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override // 默认浅拷贝
    protected CopyTest clone() {
        // 默认浅拷贝
        try {
            return (CopyTest) super.clone();
        } catch (CloneNotSupportedException e) { // 抓住的异常无需在接口声明
            throw new RuntimeException(e);
        }
    }

    // 深拷贝（新写方法，或放在 clone() 方法中）
    protected CopyTest deepClone() {
        try {
            CopyTest clone = (CopyTest) super.clone(); // 浅拷贝
            clone.setCopyInner(this.copyInner.clone()); // 浅拷贝
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        CopyTest a1 = new CopyTest(10, new CopyInner(10));

        // 浅拷贝
        CopyTest a2 = a1.clone();
        System.out.println(a1.getCopyInner() == a2.getCopyInner()); // true

        // 深拷贝
        CopyTest a3 = a1.deepClone();
        System.out.println(a1.getCopyInner() == a3.getCopyInner()); // false

        // 引用拷贝
        CopyTest a4 = a1;
        System.out.println(a1 == a4); // true
    }
}
