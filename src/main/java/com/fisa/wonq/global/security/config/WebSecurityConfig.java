package com.fisa.wonq.global.security.config;

import com.fisa.wonq.global.security.jwt.JwtAccessDeniedHandler;
import com.fisa.wonq.global.security.jwt.JwtAuthenticationEntryPoint;
import com.fisa.wonq.global.security.jwt.JwtAuthenticationFilter;
import com.fisa.wonq.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

import static com.fisa.wonq.member.domain.enums.MemberRole.ROLE_USER;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 패스워드 관련 여러 인코딩 알고리즘 사용을 제공하는 DelegatingPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * permitAll 권한을 가진 엔드포인트에 적용되는 SecurityFilterChain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChainPermitAll(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        http.securityMatchers(matchers -> matchers.requestMatchers(requestPermitAll()))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest()
                        .permitAll());
        return http.build();
    }

    /**
     * 인증 및 인가가 필요한 엔드포인트에 적용되는 SecurityFilterChain 입니다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChainAuthorized(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        http
                .securityMatchers(matchers -> matchers
                        .requestMatchers(requestHasRoleUser())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(requestHasRoleUser()).hasAuthority(ROLE_USER.name())
                        .anyRequest().authenticated() // 여기서 GUEST 권한도 처리
                )
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                    exception.accessDeniedHandler(jwtAccessDeniedHandler);
                })
                .addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider), ExceptionTranslationFilter.class);
        return http.build();
    }

    /**
     * 위에서 정의된 엔드포인트 이외에는 authenticated로 설정
     */
    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChainDefault(HttpSecurity http) throws Exception {
        configureCommonSecuritySettings(http);
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest()
                        .authenticated()
                )
                .addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider), ExceptionTranslationFilter.class)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                    exception.accessDeniedHandler(jwtAccessDeniedHandler);
                });
        return http.build();
    }

    // 인증 및 인가가 필요한 엔드포인트에 적용되는 RequestMatcher
    private RequestMatcher[] requestHasRoleUser() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/api/v1/merchant/info"),
                antMatcher("/api/v1/merchant/tables"),
                antMatcher("/api/v1/merchant/menus"),
                antMatcher("/api/v1/merchant/menus/update"),
                antMatcher("/api/v1/merchant/menus/{merchantId}/availability"),
                antMatcher("/api/v1/merchant/tables/{tableId}/status"),
                antMatcher("/api/v1/orders/daily"),
                antMatcher("/api/v1/orders/daily"),
                antMatcher("/api/v1/orders/{orderMenuId}/status"),
                antMatcher("/api/v1/merchant/qr"),
                antMatcher("/api/v1/auth/login/history"),
                antMatcher("/api/v1/merchant/info"),
                antMatcher("/api/v1/merchant/tables/{tableId}/status")
        );
        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    // permitAll 권한을 가진 엔드포인트에 적용되는 RequestMatcher
    private RequestMatcher[] requestPermitAll() {
        List<RequestMatcher> requestMatchers = List.of(
                antMatcher("/"),
                antMatcher("/health"),
                antMatcher("/test"),
                antMatcher("/swagger-ui/**"),
                antMatcher("/actuator/**"),
                antMatcher("/v3/api-docs/**"),
                antMatcher("/api/v1/auth/ocr"),
                antMatcher("/api/v1/auth/register"),
                antMatcher("/api/v1/auth/checkAccountId"),
                antMatcher("/api/v1/auth/login"),
                antMatcher("/api/v1/merchant/image"),
                antMatcher("/api/v1/merchant/menus/{merchantId}/list"),
                antMatcher("/api/v1/orders/prepare"),
                antMatcher("/api/v1/merchant/{merchantId}/overview"),
                antMatcher("/api/v1/orders/code/{orderCode}"),
                antMatcher("/api/v1/orders/code/{orderCode}/verify"),
                antMatcher("/api/orders/refund")
        );
        return requestMatchers.toArray(RequestMatcher[]::new);
    }

    private void configureCommonSecuritySettings(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
}

