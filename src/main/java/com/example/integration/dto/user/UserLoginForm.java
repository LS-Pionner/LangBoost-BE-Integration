package com.example.integration.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginForm {
    private String username;
    private String password;
    private String refreshToken;
}
