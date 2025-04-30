package com.bugcodes.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Data
public class DelayTask implements Serializable {
    private String id;
    private String topic;
    private String payload;
    private long executeTime;
    private int retryCount = 0;
    private int maxRetry = 5;
    private int priority = 0;
}
