package com.bugcodes.core;

import com.bugcodes.model.DelayTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Slf4j
@Component
public class DeadLetterQueue {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getDeadLetterKey(String topic) {
        return "dead_letter_queue:" + topic;
    }

    public void addDeadTask(DelayTask task) {
        try {
            String taskJson = objectMapper.writeValueAsString(task);
            stringRedisTemplate.opsForList().rightPush(getDeadLetterKey(task.getTopic()), taskJson);
        } catch (Exception e) {
            log.error("Add dead task failed", e);
        }
    }
}
