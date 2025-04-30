package com.bugcodes.controller;

import com.bugcodes.core.DelayQueueManager;
import com.bugcodes.model.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Slf4j
@RestController
public class TestTaskGenerator {

    @Resource
    private DelayQueueManager delayQueueManager;

    @GetMapping("/admin/generate-tasks")
    public String generateTasks(@RequestParam(value = "count", defaultValue = "1000") int count) {
        String topic = "default";

        for (int i = 0; i < count; i++) {
            DelayTask task = new DelayTask();
            task.setId(UUID.randomUUID().toString().replace("-", ""));
            task.setTopic(topic);
            task.setPayload("test-payload-" + i);

            // 触发时间：现在+随机3~10秒
            long delayMillis = ThreadLocalRandom.current().nextLong(3000, 10000);
            task.setExecuteTime(System.currentTimeMillis() + delayMillis);

            // 优先级随机：0～5
            task.setPriority(ThreadLocalRandom.current().nextInt(0, 6));

            delayQueueManager.addTask(task);
        }

        log.info("Generated {} test tasks into topic [{}]", count, topic);
        return "SUCCESS: Generated " + count + " tasks.";
    }
}
