package com.example.integration.config.jwt;

import com.example.integration.entity.User;
import com.example.integration.entity.dto.user.VerifyResult;
import com.example.integration.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class JWTCheckFilter extends BasicAuthenticationFilter {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public JWTCheckFilter(AuthenticationManager authenticationManager, UserService userService, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        String uri = request.getRequestURI();
        // 아래 경로에 대해서는 필터 X
        if (uri.equals("/api/v1/auth/register")
                || uri.equals("/api/v1/auth/login")
                || uri.equals("/api/v1/auth/email-check")
                || uri.equals("/api/v1/auth/reissue")
                || uri.startsWith("/api/v1/public")) {
            chain.doFilter(request, response);
            return;
        }

        // 토큰이 존재하지 않는 경우
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            log.error("Invalid Authorization header format. Header: {}", bearer);
            // EntryPoint에서 예외 처리
            throw new JWTAuthenticationException("Authorization header is missing");
        }

        // 전달 받은 Jwt Access Token
        String token = bearer.substring("Bearer ".length());

        // 토큰 검증
        VerifyResult result = jwtUtil.verify(token);

        if (result.isSuccess()) {
            // 유저 검증
            User user = (User) userService.loadUserByUsername(result.username());
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );

            // SecurityContext에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(userToken);
            log.info("Token successfully verified for user: {}. Request URI: {}", user.getUsername(), request.getRequestURI());
            chain.doFilter(request, response);
        } else {
            // 검증 실패 시 로그 기록
            log.error("Token verification failed for token: {}.", token);

            throw new JWTAuthenticationException("Invalid or expired token.");
        }
    }

}
