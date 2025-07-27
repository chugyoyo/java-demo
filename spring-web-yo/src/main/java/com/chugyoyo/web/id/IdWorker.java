package com.chugyoyo.web.id;

import com.chugyoyo.web.common.SystemClock;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

public class IdWorker {
    // 基准时间戳（毫秒），用于计算时间偏移量
    private final static long twepoch = 1630734792540L;

    // 序列号占用位数（12位，支持4096个序列号/ms）
    private final static int sequenceBits = 12;

    // 工作节点ID占用位数（10位，支持1024个节点）
    private final static int workerIdBits = 10;

    // 时间戳占用位数（41位，约69年有效期）
    private final static int timestampBits = 41;

    // 当前工作节点ID
    private final long workerId;

    // 最大工作节点ID值（1023）
    private final static int maxWorkerId = ~(-1 << workerIdBits);

    // 序列号掩码（4095），用于限制序列号范围
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 上次生成ID的时间戳
    private static long lastTimestamp = -1L;

    // 当前序列号
    private long sequence = 0L;

    // 构造函数
    public IdWorker(Long workerId) {
        // 如果未提供workerId，自动生成
        if (workerId == null) {
            workerId = generateWorkerId();
        }
        // 校验workerId范围
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can’t be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    // 生成全局唯一ID（线程安全）
    public synchronized long nextId() { // 🔒
        long timestamp = timeGen();  // 获取当前时间戳

        // 时钟回拨检查（防止时间倒退导致ID重复）
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 同一毫秒内的处理逻辑
        if (lastTimestamp == timestamp) {
            // 序列号自增并应用掩码
            sequence = (sequence + 1) & sequenceMask;
            // 序列号溢出处理（等待下一毫秒）
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 新毫秒重置序列号
            sequence = 0L;
        }
        lastTimestamp = timestamp;  // 更新最后时间戳

        // 组合ID各部分（时间戳 | 工作节点 | 序列号）
        return ((timestamp - twepoch) << (workerIdBits + sequenceBits))
                | (workerId << sequenceBits)
                | sequence;
    }

    // 阻塞直到下一毫秒
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    // 获取当前系统时间（毫秒）
    private long timeGen() {
        return SystemClock.millisClock().now();
    }

    // 生成工作节点ID
    private long generateWorkerId() {
        try {
            // 优先基于MAC地址生成
            return generateWorkerIdBaseOnMac();
        } catch (Exception e) {
            // MAC生成失败时使用随机生成
            return generateRandomWorkerId();
        }
    }

    // 基于MAC地址生成工作节点ID
    private long generateWorkerIdBaseOnMac() throws Exception {
        Enumeration<NetworkInterface> all = NetworkInterface.getNetworkInterfaces();
        while (all.hasMoreElements()) {
            NetworkInterface networkInterface = all.nextElement();
            // 过滤无效网络接口
            boolean isLoopback = networkInterface.isLoopback();
            boolean isVirtual = networkInterface.isVirtual();
            byte[] mac = networkInterface.getHardwareAddress();
            if (isLoopback || isVirtual || mac == null) {
                continue;
            }
            // 提取MAC地址后两字节（取第5字节低2位 + 第6字节）
            return ((mac[4] & 0B11) << 8) | (mac[5] & 0xFF);
        }
        throw new RuntimeException("no available mac found");
    }

    // 随机生成工作节点ID（0~1023）
    private long generateRandomWorkerId() {
        return new Random().nextInt(maxWorkerId + 1);
    }

    // 获取当前工作节点ID
    public long getWorkerId() {
        return workerId;
    }

}