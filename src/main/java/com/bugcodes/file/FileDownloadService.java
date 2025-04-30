package com.bugcodes.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * @author zbj
 * @date 2025/4/30
 */
@Service
public class FileDownloadService {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String FILE_META_PREFIX = "file_meta:";

    public boolean checkFileExists(String fileName) {
        String publicPath = fileStorageProperties.getPublicStaticDir() + "/" + fileName;
        return Files.exists(new File(publicPath).toPath());
    }

    public long getFileTtl(String fileName) {
        return redisTemplate.getExpire(FILE_META_PREFIX + fileName, TimeUnit.SECONDS);
    }

    public String ensureFileReady(String fileName) {
        String publicPath = fileStorageProperties.getPublicStaticDir() + "/" + fileName;
        File publicFile = new File(publicPath);

        if (publicFile.exists() && redisTemplate.hasKey(FILE_META_PREFIX + fileName)) {
            long ttl = getFileTtl(fileName);
            if (ttl > fileStorageProperties.getRenewThresholdSeconds()) {
                return generateDownloadUrl(fileName);
            }
        }

        // 拷贝文件
        String sourcePath = fileStorageProperties.getOriginalDir() + "/" + fileName;
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new RuntimeException("源文件不存在");
        }
        try {
            FileCopyUtils.copy(sourceFile, publicFile);
            redisTemplate.opsForValue().set(
                    FILE_META_PREFIX + fileName, "1",
                    fileStorageProperties.getValidSeconds(), TimeUnit.SECONDS
            );
            // 此处可集成延迟队列注册清理逻辑
        } catch (Exception e) {
            throw new RuntimeException("文件拷贝失败", e);
        }

        return generateDownloadUrl(fileName);
    }

    public String generateDownloadUrl(String fileName) {
        return "/static/" + fileName;
    }
}
