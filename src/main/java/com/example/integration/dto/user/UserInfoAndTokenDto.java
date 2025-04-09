package com.example.integration.dto.user;

public record UserInfoAndTokenDto(
        UserInfoDto userInfoDto,
        TokenDto tokenDto
) {
}
