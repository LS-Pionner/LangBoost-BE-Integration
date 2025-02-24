package com.example.integration.config;

import com.example.integration.config.exception.CustomAccessDeniedHandler;
import com.example.integration.config.exception.CustomAuthenticationEntryPoint;
import com.example.integration.config.jwt.JWTCheckFilter;
import com.example.integration.config.jwt.JWTUtil;
import com.example.integration.entity.RoleType;
import com.example.integration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;

    private final JWTUtil jwtUtil;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CorsConfigurationSource corsConfigurationSource;

    private String[] whiteList = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/email-check",
            "/api/v1/auth/reissue",
            "/api/v1/public/*"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * static으로 할당되지 않으면 SecurityConfig와 UserService 간에 순환 참조 에러 발생
     * @return
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager, userService, jwtUtil);

        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 관리 정책 설정 (세션 생성 x)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(whiteList).permitAll() // 이 경로들은 인증 없이 접근 가능
                                .requestMatchers("/api/v1/auth/logout").authenticated() // 로그아웃은 권한에 상관없이 접근 가능
                                .requestMatchers("/api/v1/auth/**").hasAnyRole(RoleType.USER.name(), RoleType.ADMIN.name()) // 나머지 모든 요청은 USER 권한 필요
                                .anyRequest().permitAll()
                ).exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterAfter(checkFilter, ExceptionTranslationFilter.class); // JWT 검증 필터 추가
        return http.build();
    }

}
