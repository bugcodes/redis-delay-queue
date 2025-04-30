package com.bugcodes.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zbj
 * @date 2025/4/30
 */
@Service
public class DownloadShortLinkService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "short_link:";

    public String createShortLink(String fileName, String userId) {
        String shortKey = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        long expire = System.currentTimeMillis() / 1000 + 10 * 60;
        String token = SignatureUtil.generateToken(fileName, expire);

        String value = fileName + "|" + token + "|" + expire;
        redisTemplate.opsForValue().set(PREFIX + shortKey, value, 10, TimeUnit.MINUTES);

        return shortKey;
    }

    public String resolveShortLink(String shortKey) {
        String value = redisTemplate.opsForValue().get(PREFIX + shortKey);
        if (value == null) return null;

        redisTemplate.delete(PREFIX + shortKey);
        return value;
    }
}
