package com.bugcodes.script;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zbj
 * @date 2025/4/28
 */
@Component
public class LuaScripts {

    public DefaultRedisScript<String> fetchAndRemoveScript;

    @PostConstruct
    public void init() {
        fetchAndRemoveScript = new DefaultRedisScript<>();
        fetchAndRemoveScript.setScriptText(
                "local key = KEYS[1] " +
                        "local now = tonumber(ARGV[1]) " +
                        "local tasks = redis.call('zrangebyscore', key, 0, now, 'LIMIT', 0, 1) " +
                        "if #tasks == 0 then return nil end " +
                        "redis.call('zrem', key, tasks[1]) " +
                        "return tasks[1]"
        );
        fetchAndRemoveScript.setResultType(String.class);
    }
}
