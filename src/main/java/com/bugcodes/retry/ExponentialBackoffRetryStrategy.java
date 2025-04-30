package com.bugcodes.retry;

/**
 * @author zbj
 * @date 2025/4/28
 */
public class ExponentialBackoffRetryStrategy implements RetryStrategy{

    private final long baseMillis;

    public ExponentialBackoffRetryStrategy(long baseMillis) {
        this.baseMillis = baseMillis;
    }

    @Override
    public long nextDelayMillis(int currentRetryCount) {
        return baseMillis * (1L << (currentRetryCount - 1));
    }
}
