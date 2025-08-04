package com.chugyoyo.base.javabase.serialization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

// 5. 测试类
public class SerializationTest {
    public static void main(String[] args) {
        // 创建测试对象
        User user = new User("john_doe", "secret123", "50000000");
        System.out.println("原始对象: " + user);

        // 创建处理器（使用JDK策略）
        SerializationProcessor<User> processor =
                new SerializationProcessor<>(new JdkSerializationStrategy<>());

        try {
            // 序列化测试
            byte[] data = processor.serializeObject(user);
            System.out.println("序列化字节长度: " + data.length);

            // 反序列化测试
            User restoredUser = processor.deserializeObject(data);
            System.out.println("反序列化对象: " + restoredUser);

            // 验证transient字段
            System.out.println("密码字段是否为空? " + (restoredUser.getPassword() == null));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// 4. 测试实体类（带transient示例）
@Getter
@AllArgsConstructor
@ToString
class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private transient String password; // 1️⃣ transient 不被序列化
    private static final Integer userNameMaxLength = 20; // 2️⃣ static 不被序列化
    private String idCard; // 4️⃣

    // 3️⃣ writeObject/readObject 自定义序列化逻辑（可选）
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // 可添加额外处理
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        password = "null";
    }
}