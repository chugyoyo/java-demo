//package com.chugyoyo.base.javabase.serialization;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//
//// 新增Kryo策略实现（不修改已有代码）
//public class KryoSerializationStrategy<T> implements SerializationStrategy<T> {
//    private final Kryo kryo = new Kryo();
//
//    @Override
//    public byte[] serialize(T obj) {
//        try (Output output = new Output(new ByteArrayOutputStream())) {
//            kryo.writeObject(output, obj);
//            return output.toBytes();
//        }
//    }
//
//    @Override
//    public T deserialize(byte[] data) {
//        try (Input input = new Input(new ByteArrayInputStream(data))) {
//            return kryo.readObject(input, objClass);
//        }
//    }
//}
