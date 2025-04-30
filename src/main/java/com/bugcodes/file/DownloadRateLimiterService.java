package com.bugcodes.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author zbj
 * @date 2025/4/30
 */
@Service
public class DownloadRateLimiterService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String PREFIX = "download_limit:";
    private static final int MAX_DOWNLOADS = 3;
    private static final int TIME_WINDOW_SECONDS = 60;

    public boolean tryAcquire(String userId, String fileId) {
        String key = PREFIX + userId + ":" + fileId;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, TIME_WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        return count <= MAX_DOWNLOADS;
    }
}
