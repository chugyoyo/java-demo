package com.chugyoyo.base.javabase.serialization;

import java.io.IOException;

// 3. 序列化处理器（开闭原则核心）
public class SerializationProcessor<T> {
    private final SerializationStrategy<T> strategy;

    // 通过构造器注入策略
    public SerializationProcessor(SerializationStrategy<T> strategy) {
        this.strategy = strategy;
    }

    // 序列化操作
    public byte[] serializeObject(T obj) throws IOException {
        return strategy.serialize(obj);
    }

    // 反序列化操作
    public T deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        return strategy.deserialize(data);
    }
}