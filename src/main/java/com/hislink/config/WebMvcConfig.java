package com.hislink.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path projectUploadDir = Paths.get(uploadProperties.getDir(), "projects").toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/projects/**")
                .addResourceLocations("file:" + projectUploadDir + "/");
    }
}
