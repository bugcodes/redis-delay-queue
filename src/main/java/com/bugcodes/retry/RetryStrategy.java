package com.bugcodes.retry;

/**
 * @author zbj
 * @date 2025/4/28
 */
public interface RetryStrategy {

    /**
     * 根据当前重试次数，计算下次延迟多少毫秒
     */
    long nextDelayMillis(int currentRetryCount);
}
