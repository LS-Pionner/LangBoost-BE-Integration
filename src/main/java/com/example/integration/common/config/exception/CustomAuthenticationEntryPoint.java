package com.example.integration.common.config.exception;

import com.example.integration.common.config.jwt.JwtTokenExpiredException;
import com.example.integration.response.ApiResponse;
import com.example.integration.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("Not Authenticated Request");
        log.error("Request Uri : {}", request.getRequestURI());

        // body 설정
        ApiResponse<Object> apiResponse;

        if (authException instanceof JwtTokenExpiredException) {
            apiResponse = ApiResponse.fail(ErrorCode.EXPIRED_TOKEN);
        } else {
            apiResponse = ApiResponse.fail(ErrorCode.INVALID_TOKEN);
        }

        String responseBody = objectMapper.writeValueAsString(apiResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
