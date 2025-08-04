package com.chugyoyo.base.javabase.serialization;

import java.io.IOException;

// 1. 序列化策略接口（核心抽象）
public interface SerializationStrategy<T> {

    byte[] serialize(T obj) throws IOException;

    T deserialize(byte[] data) throws IOException, ClassNotFoundException;
}