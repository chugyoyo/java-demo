package com.chugyoyo.base.javabase.optional;

import java.util.Optional;

public class OptionalDemo {

    public static String getNullValue() {
        return null;
    }

    public static String getNotNullValue() {
        return "chugyoyo";
    }

    public static void test1() {
        Optional<String> opt3 = Optional.empty();          // 空 Optional

        Optional<String> opt = Optional.ofNullable(getNullValue());
        if (opt.isPresent()) {
            System.out.println("Value is: " + opt.get());
        } else {
            System.out.println("Value is absent");
        }
    }

    public static void test2() {
        Optional<String> opt1 = Optional.ofNullable(getNullValue());
        opt1.ifPresent(v -> System.out.println("Value is: " + v));
    }

    public static void test3() {

        String val1 = Optional.ofNullable(getNullValue()).orElse(getNotNullValue());
        System.out.println("orElse: " + val1);

        String val2 = Optional.ofNullable(getNullValue()).orElseGet(() -> getNotNullValue());
        System.out.println("orElseGet: " + val2);
    }

    public static void test4 () {
        try {
            String val3 = Optional.ofNullable(getNullValue()).orElseThrow(() -> new RuntimeException("Value is null!"));
            System.out.println("orElseThrow: " + val3);
        } catch (RuntimeException e) {
            System.out.println("orElseThrow: " + e.getMessage());
        }
    }

    // 6. map / flatMap（转换值）
    public static void test5() {
        Optional<String> opt1 = Optional.ofNullable(getNotNullValue());
        Optional<String> upper = opt1.map((str) -> {
            return str.toUpperCase();
        });
        System.out.println("map to upper: " + upper.orElse("N/A"));

        Optional<String> opt2 = Optional.ofNullable(getNotNullValue());
        Optional<String> flatMapped = opt2.flatMap(v -> Optional.of(v + " World"));
        System.out.println("flatMap: " + flatMapped.get());
    }

    // filter
    public static void test6() {
        Optional<String> opt1 = Optional.ofNullable(getNotNullValue());
        Optional<String> filtered = opt1.filter(v -> v.startsWith("H"));
        System.out.println("filter result: " + filtered.orElse("Not match"));
    }

    public static void test7() {
        // 8. 链式调用
        String result = Optional.ofNullable(getNotNullValue())
                .map(String::toUpperCase)
                .filter(s -> s.length() > 3)
                .flatMap(v -> Optional.of(v + " World"))
                .orElse("EMPTY");
        System.out.println("Chained result: " + result);
    }

    public static void test8() {
        // 9. 复杂对象安全取值（避免 NPE）
        Person p = new Person("Tom", null);
        String city = Optional.ofNullable(p)
                .map(Person::getAddress)
                .map(Address::getCity)
                .orElse("Unknown City");
        System.out.println("City: " + city);
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
        test8();
    }

    static class Person {
        private String name;
        private Address address;

        public Person(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    static class Address {
        private String city;

        public Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }
}