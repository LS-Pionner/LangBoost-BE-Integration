package com.example.integration.entity.dto.user;

public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
