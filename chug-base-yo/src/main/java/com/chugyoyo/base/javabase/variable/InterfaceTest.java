package com.chugyoyo.base.javabase.variable;

public strictfp class InterfaceTest {

    interface Test {
        // 普通方法，默认是 public abstract
        public abstract void test();
        //  `default` 方法用于提供接口方法的默认实现，可以在实现类中被覆盖。
        default void test2() {
            System.out.println("test2");
        }
        // `static` 方法用于提供接口方法的静态实现，不能在实现类中被覆盖。
        static void test3() {
            System.out.println("test3");
        }
        // JDK9 之后，private 方法可以用于在接口内部共享代码，不对外暴露。
//        private void test4() {
//            System.out.println("test4");
//        }
    }

    static class TestImpl implements Test {

        @Override
        public void test() {
            System.out.println("test");
        }
    }

    public static void main(String[] args) {
        Test test = new TestImpl();
        test.test();
        test.test2();
        Test.test3();
    }
}
