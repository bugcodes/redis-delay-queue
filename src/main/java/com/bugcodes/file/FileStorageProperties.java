package com.bugcodes.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zbj
 * @date 2025/4/30
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {

    private String originalDir;
    private String publicStaticDir;
    private long validSeconds;
    private long renewThresholdSeconds;
}
