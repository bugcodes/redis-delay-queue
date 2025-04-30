package com.bugcodes.controller;

import com.bugcodes.core.DeadLetterQueue;
import com.bugcodes.model.DelayTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DeadLetterQueue deadLetterQueue;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getQueueKey(String topic) {
        return "delay_queue:" + topic;
    }

    private String getDeadLetterKey(String topic) {
        return "dead_letter_queue:" + topic;
    }

    @GetMapping
    public String dashboard(Model model) {
        String topic = "default";
        Long queueSize = stringRedisTemplate.opsForZSet().size(getQueueKey(topic));
        Long deadSize = stringRedisTemplate.opsForList().size(getDeadLetterKey(topic));
        model.addAttribute("queueSize", queueSize == null ? 0 : queueSize);
        model.addAttribute("deadSize", deadSize == null ? 0 : deadSize);
        return "dashboard";
    }

    @GetMapping("/dead-letters")
    public String deadLetters(Model model) {
        String topic = "default";
        List<String> deadTasks = stringRedisTemplate.opsForList().range(getDeadLetterKey(topic), 0, 99);
        model.addAttribute("deadTasks", deadTasks);
        return "dead-letters";
    }

    @PostMapping("/retry")
    @ResponseBody
    public String retryDeadTask(@RequestParam String taskJson) {
        try {
            DelayTask task = objectMapper.readValue(taskJson, DelayTask.class);
            task.setRetryCount(0); // 重置重试次数
            task.setExecuteTime(System.currentTimeMillis() + 3000); // 3秒后重试
            stringRedisTemplate.opsForZSet().add(getQueueKey(task.getTopic()), objectMapper.writeValueAsString(task), task.getExecuteTime());
            return "SUCCESS";
        } catch (Exception e) {
            log.error("Retry dead task error", e);
            return "FAIL";
        }
    }

}
