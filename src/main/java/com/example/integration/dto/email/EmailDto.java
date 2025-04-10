package com.example.integration.dto.email;

import javax.validation.constraints.NotEmpty;

public record EmailDto(
        @NotEmpty(message = "Purpose cannot be empty")
        String purpose,

        @NotEmpty(message = "Email cannot be empty")
        String mail,

        String verifyCode
) {
}
