package com.example.integration.dto.user;

public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
