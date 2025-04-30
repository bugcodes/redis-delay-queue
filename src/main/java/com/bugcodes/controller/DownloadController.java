package com.bugcodes.controller;

import com.bugcodes.file.DownloadRateLimiterService;
import com.bugcodes.file.DownloadShortLinkService;
import com.bugcodes.file.FileDownloadService;
import com.bugcodes.file.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author zbj
 * @date 2025/4/30
 */
@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadController {

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private DownloadRateLimiterService rateLimiterService;

    @Autowired
    private DownloadShortLinkService shortLinkService;

    @GetMapping("/gen-short-link")
    public String generateShortLink(@RequestParam String fileName, @RequestParam String userId) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (!rateLimiterService.tryAcquire(userId, fileName)) {
            throw new RuntimeException("下载频率过高，请稍后再试！");
        }

        String url = fileDownloadService.ensureFileReady(fileName);
        String shortKey = shortLinkService.createShortLink(fileName, userId);
        return "/download/short/" + shortKey;
    }

    @GetMapping("/short/{shortKey}")
    public String resolveShortLink(@PathVariable String shortKey) {
        String value = shortLinkService.resolveShortLink(shortKey);
        if (value == null) {
            throw new RuntimeException("链接已失效或已使用！");
        }

        String[] parts = value.split("\\|");
        String fileName = parts[0];
        String token = parts[1];
        long expire = Long.parseLong(parts[2]);

        if (!SignatureUtil.verifyToken(fileName, expire, token)) {
            throw new RuntimeException("无效的下载请求！");
        }

        if (System.currentTimeMillis() / 1000 > expire) {
            throw new RuntimeException("下载链接已过期！");
        }

        return "redirect:/static/" + fileName + "?token=" + token + "&expire=" + expire;
    }
}
