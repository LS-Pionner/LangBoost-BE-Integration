package com.example.integration.service;

import com.example.integration.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final RedisUtil redisUtil;

    /**
     * Jwt Refresh Token을 Redis에 저장
     * @param email
     * @param refreshToken
     * @param duration
     */
    public void saveRefreshTokenToRedis(String email, String refreshToken, long duration) {
        String key = "refresh_token:" + email;

        // refresh token을 Redis에 저장, 만료 시간도 설정
        redisUtil.setDataExpire(key, refreshToken, duration);
    }

    /**
     * Jwt Refresh Token이 유효한지 확인
     * @param email
     * @param refreshToken
     * @return
     */
    public boolean isRefreshTokenValid(String email, String refreshToken) {
        String key = "refresh_token:" + email;

        // Redis에서 해당 키의 값을 가져옴
        String storedRefreshToken = redisUtil.getData(key);

        // Redis에 저장된 refresh token 값이 null이 아니고, 입력된 refresh token과 동일한지 확인
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
    }

    /**
     * Jwt Refresh Token을 Redis에서 삭제
     * @param username
     */
    public void deleteRefreshToken(String username) {
        String key = "refresh_token:" + username;

        // Redis에서 refresh token 삭제
        redisUtil.deleteData(key);
    }
}
