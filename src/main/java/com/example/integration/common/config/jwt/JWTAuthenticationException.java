package com.example.integration.common.config.jwt;
import org.springframework.security.core.AuthenticationException;


public class JWTAuthenticationException extends AuthenticationException {

    public JWTAuthenticationException(String message) {
        super(message);
    }

    public JWTAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

