package com.chugyoyo.base.javabase.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MyPublisherTest {

    @Autowired
    private MyPublisher myPublisher;

    @PostConstruct
    public void init() {
        myPublisher.publish();
    }
}
