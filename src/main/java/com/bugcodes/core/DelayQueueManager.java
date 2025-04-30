package com.bugcodes.core;

import com.bugcodes.model.DelayTask;
import com.bugcodes.script.LuaScripts;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Slf4j
@Component
public class DelayQueueManager {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private LuaScripts luaScripts;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getQueueKey(String topic, String taskId) {
        int shardId = ShardUtil.getShardId(taskId);
        return "delay_queue:" + topic + ":" + shardId;
    }

    public void addTask(DelayTask task) {
        try {
            String taskJson = objectMapper.writeValueAsString(task);
            long score = task.getExecuteTime() - task.getPriority() * 1000L;
            stringRedisTemplate.opsForZSet().add(getQueueKey(task.getTopic(), task.getId()), taskJson, score);
        } catch (Exception e) {
            log.error("Add task failed", e);
        }
    }

    public DelayTask fetchDueTask(String topic, int shardId) {
        long now = System.currentTimeMillis();
        String queueKey = "delay_queue:" + topic + ":" + shardId;
        String taskJson = stringRedisTemplate.execute(luaScripts.fetchAndRemoveScript,
                Collections.singletonList(queueKey), String.valueOf(now));
        if (taskJson == null) return null;
        try {
            return objectMapper.readValue(taskJson, DelayTask.class);
        } catch (Exception e) {
            log.error("Parse task failed", e);
            return null;
        }
    }
}
