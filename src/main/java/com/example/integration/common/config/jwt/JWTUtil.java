package com.example.integration.common.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.integration.entity.User;
import com.example.integration.dto.user.VerifyResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JWTUtil {
    private final Algorithm algorithm;
    private final long authTime;
    private final long refreshTime;

    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.auth-time}") long authTime,
            @Value("${jwt.refresh-time}") long refreshTime
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.authTime = authTime;
        this.refreshTime = refreshTime;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    /**
     * Jwt Access Token 생성
     * @param user
     * @return
     */
    public String makeAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("iat", Instant.now().getEpochSecond()) // 발급 시간
                .withClaim("exp", Instant.now().getEpochSecond() + authTime) // 만료 시간
                .sign(algorithm);
    }

    /**
     * Jwt Refresh Token 생성
     * @param user
     * @return
     */
    public String makeRefreshToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("iat", Instant.now().getEpochSecond()) // 발급 시간
                .withClaim("exp", Instant.now().getEpochSecond() + refreshTime) // 만료 시간
                .sign(algorithm);
    }

    /**
     * Jwt 토큰 검증
     * @param token
     * @return
     */
    public VerifyResult verify(String token) {
        try {
            // 검증 성공
            DecodedJWT verify = JWT.require(algorithm).build().verify(token);
            return new VerifyResult(TokenStatus.SUCCESS, verify.getSubject());
        } catch (JWTVerificationException ex) {
            // 검증 실패
            DecodedJWT decode = JWT.decode(token);

            if (decode.getExpiresAt() != null && decode.getExpiresAt().before(new Date())) {
                // 만료된 경우
                return new VerifyResult(TokenStatus.EXPIRED, decode.getSubject());
            }

            return new VerifyResult(TokenStatus.INVALID, decode.getSubject());
        }
    }
}
