package com.example.integration.entity.dto.user;

public record UserInfoAndTokenDto(
        UserInfoDto userInfoDto,
        TokenDto tokenDto
) {
}
