package com.bugcodes.scheduler;

import com.bugcodes.core.DelayQueueManager;
import com.bugcodes.executor.DelayTaskExecutor;
import com.bugcodes.model.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Slf4j
@Component
public class DelayTaskScheduler {

    @Resource
    private DelayQueueManager delayQueueManager;

    @Resource
    private DelayTaskExecutor delayTaskExecutor;

    private static final int SHARD_COUNT = 32;

    @Scheduled(fixedRate = 1000)
    public void pollTasks() {
        String topic = "default";

        for (int shardId = 0; shardId < SHARD_COUNT; shardId++) {
            while (true) {
                DelayTask task = delayQueueManager.fetchDueTask(topic, shardId);
                if (task == null) {
                    break;
                }
                delayTaskExecutor.getExecutor().execute(() -> {
                    try {
                        processTask(task);
                    } catch (Exception e) {
                        log.error("Process task failed", e);
                        // 重试或死信
                    }
                });
            }
        }
    }

    private void processTask(DelayTask task) {
        log.info("Processing task id={}, payload={}", task.getId(), task.getPayload());
        // TODO: 实际业务逻辑处理
    }
}
