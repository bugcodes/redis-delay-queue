package com.bugcodes.core;

/**
 * @author zbj
 * @date 2025/4/28
 */
public class ShardUtil {
    private static final int SHARD_COUNT = 32;

    public static int getShardId(String taskId) {
        return Math.abs(taskId.hashCode()) % SHARD_COUNT;
    }
}
