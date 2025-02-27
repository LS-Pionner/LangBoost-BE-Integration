package com.example.integration.entity.dto.user;

import com.example.integration.config.jwt.TokenStatus;

public record VerifyResult(TokenStatus status, String username) {
}
