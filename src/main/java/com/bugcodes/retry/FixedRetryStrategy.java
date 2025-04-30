package com.bugcodes.retry;

/**
 * @author zbj
 * @date 2025/4/28
 */
public class FixedRetryStrategy implements RetryStrategy{

    private final long intervalMillis;

    public FixedRetryStrategy(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    @Override
    public long nextDelayMillis(int currentRetryCount) {
        return intervalMillis;
    }
}
