package com.chugyoyo.version;

/**
 * 分布式版本号生成器
 *
 * @author chugyoyo
 * @since 2025/7/14
 */
public class DistributedVersionGenerator {
    private final int nodeId;     // 节点唯一ID (e.g., A=1, B=2)
    private long lastPhysical = 0;
    private int logical = 0;      // 逻辑计数器

    public DistributedVersionGenerator(int nodeId) {
        this.nodeId = nodeId;
    }

    public synchronized long generateVersion() {
        long currentPhysical = System.currentTimeMillis();

        // 处理物理时钟前进
        if (currentPhysical > lastPhysical) {
            lastPhysical = currentPhysical;
            logical = 0;
        }
        // 处理时钟回拨或相同毫秒
        else {
            // 时钟回拨时使用逻辑递增
            if (currentPhysical < lastPhysical) {
                currentPhysical = lastPhysical;
            }
            logical++;
        }

        // 组合版本号：物理时间(42位) | 节点ID(10位) | 逻辑计数器(12位)
        return ((currentPhysical << 22)
                | ((long) nodeId << 12)
                | logical);
    }

    // 处理远程版本号（在同步消息时）
    public synchronized void observeRemoteVersion(long remoteVersion) {
        long remotePhysical = remoteVersion >>> 22;
        int remoteLogical = (int) (remoteVersion & 0xFFF);

        if (remotePhysical > lastPhysical) {
            lastPhysical = remotePhysical;
            logical = 0;
        } else if (remotePhysical == lastPhysical) {
            logical = Math.max(logical, remoteLogical);
        }
    }
}