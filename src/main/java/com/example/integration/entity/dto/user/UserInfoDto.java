package com.example.integration.entity.dto.user;

public record UserInfoDto(
        Long id,
        String email,
        String username,
        String password,
        boolean enabled,
        boolean isAdmin
) {
}
