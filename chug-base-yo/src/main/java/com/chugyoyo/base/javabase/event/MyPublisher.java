package com.chugyoyo.base.javabase.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MyPublisher {

    @Autowired
    private ApplicationEventPublisher publisher;

    public void publish() {
        publisher.publishEvent(new MyEvent(this, "Hello Spring Event"));
    }
}