package com.fisa.wonq.global.config;

import com.fisa.wonq.global.security.resolver.CurrentAccountArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CurrentAccountArgumentResolver currentAccountArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentAccountArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 개발용으로 모든 도메인 허용, 운영 시 아래 주석 해제
//                .allowedOrigins(getAllowOrigins())
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type", "ngrok-skip-browser-warning")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true)
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials") // 응답 헤더 노출
                .maxAge(3600);
    }

    private String[] getAllowOrigins() {
        return Arrays.asList(
                "http://localhost:3000",
                "https://localhost:3000",
                "https://dev.wonq.store",
                "https://api.wonq.store",
                "https://wonq.store",
                "https://coherent-midge-probably.ngrok-free.app"
        ).toArray(String[]::new);
    }
}
