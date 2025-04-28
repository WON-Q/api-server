package com.fisa.wonq.global.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 파일 하나 최대 50MB
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        // 요청 전체 최대 50MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        return factory.createMultipartConfig();
    }
}
