package com.hislink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String dir = "uploads";
    private String publicBaseUrl = "http://localhost:8080";
    private long maxFileSizeBytes = 5 * 1024 * 1024;
    private int maxImagesPerProject = 10;
}
