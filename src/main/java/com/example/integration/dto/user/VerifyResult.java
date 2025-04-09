package com.example.integration.dto.user;

import com.example.integration.common.config.jwt.TokenStatus;

public record VerifyResult(TokenStatus status, String username) {
}
