package com.chugyoyo.web.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CyclicDependenceTest {

    @Autowired
    private BeanA beanA;

    @Autowired
    private BeanB beanB;

    @PostConstruct
    public void init() {
        System.out.println(beanA);
        System.out.println(beanB.getName());
    }

    @Component
    public static class BeanA {

        private BeanA beanA;

        @Autowired
        public BeanA(@Lazy BeanA beanA) { // 关键：@Lazy 在构造器参数上
            this.beanA = beanA;
        }

//        @PostConstruct
//        public void init() {
//            System.out.println(beanA);
//        }



    }

    @Component
    public static class BeanB {

        @Autowired
        @Lazy
        private BeanB beanB;

        @Value("123")
        private String name;

//        @PostConstruct
//        public void init() {
//            System.out.println(beanB);
//        }

        public String getName() {
            return name;
        }
    }
}
