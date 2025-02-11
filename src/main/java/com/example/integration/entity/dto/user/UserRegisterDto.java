package com.example.integration.entity.dto.user;

import javax.validation.constraints.NotEmpty;

public record UserRegisterDto(
        @NotEmpty(message = "Enter an email")
        String email,

        @NotEmpty(message = "Enter a password")
        String password
) {
}
