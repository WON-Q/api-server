package com.fisa.wonq.global.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class PgClientConfig {

    @Value("${app.pg.accessKey}")
    private String accessKey;

    @Value("${app.pg.secretKey}")
    private String secretKey;

    /**
     * 앱 카드사 API 호출 시 필요한 헤더를 설정하는 RequestInterceptor Bean
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor appCardRequestInterceptor() {
        return requestTemplate -> {
            String str = accessKey + ":" + secretKey;
            String token = java.util.Base64.getEncoder().encodeToString(str.getBytes());

            requestTemplate.header("Authorization", "Basic " + token); // 헤더에 Authorization 추가
            requestTemplate.header("Content-Type", "application/json"); // Content-Type 헤더 추가
        };
    }


}
