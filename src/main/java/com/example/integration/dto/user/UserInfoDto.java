package com.example.integration.dto.user;

public record UserInfoDto(
        Long id,
        String email,
        String username,
        String password,
        boolean enabled
) {
}
