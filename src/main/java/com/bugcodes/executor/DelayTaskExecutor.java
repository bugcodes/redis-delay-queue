package com.bugcodes.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Slf4j
@Component
public class DelayTaskExecutor {

    private ThreadPoolTaskExecutor executor;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);         // 核心线程数
        executor.setMaxPoolSize(32);          // 最大线程数
        executor.setQueueCapacity(1000);      // 队列容量
        executor.setThreadNamePrefix("delay-task-exec-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()); // 饱和策略
        executor.initialize();
    }

    public Executor getExecutor() {
        return executor;
    }
}
