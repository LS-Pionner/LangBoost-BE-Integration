package com.example.integration.service;

import com.example.api.response.CustomException;
import com.example.integration.config.exception.ErrorCode;
import com.example.integration.config.jwt.JWTUtil;
import com.example.integration.config.jwt.TokenStatus;
import com.example.integration.entity.RoleType;
import com.example.integration.entity.User;
import com.example.integration.entity.dto.user.*;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    /**
     * 사용자 회원가입
     * @param userRegisterDto
     * @return
     */
    @Transactional
    public User createUser(UserRegisterDto userRegisterDto) {
        checkEmailExists(userRegisterDto.email());

        User user = User.builder()
                .email(userRegisterDto.email())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .roleType(RoleType.NOBODY)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * 이메일 중복 체크
     * @param email
     */
    @Transactional(readOnly = true)
    public void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 사용자 로그인
     * @param loginForm
     * @return
     */
    @Transactional
    public UserInfoAndTokenDto loginUser(UserLoginForm loginForm) {
        loadUserByUsername(loginForm.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.makeAccessToken(user);
        String refreshToken = jwtUtil.makeRefreshToken(user);
        tokenService.saveRefreshTokenToRedis(user.getEmail(), refreshToken, jwtUtil.getRefreshTime());

        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        boolean isAdmin = user.getRoleType().equals(RoleType.ADMIN);
        UserInfoDto userInfoDto = new UserInfoDto(user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), user.isEnabled(), isAdmin);

        return new UserInfoAndTokenDto(userInfoDto, tokenDto);
    }

    /**
     * 사용자 로그아웃
     * @return
     */
    @Transactional
    public boolean logoutUser() {
        // SecurityContext에서 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // 사용자의 이름 또는 ID

            // Refresh Token 삭제
            tokenService.deleteRefreshToken(username);

            // SecurityContext 초기화
            SecurityContextHolder.clearContext();
            return true;
        }

        // 인증 정보가 없는 경우 (로그아웃 실패)
        throw new CustomException(ErrorCode.NOT_FOUND_USER); // 사용자 정보 없음
    }

    /**
     * Jwt 토큰 재발급
     * @param refreshToken
     * @return
     */
    @Transactional
    public TokenDto reissueToken(String refreshToken) {
        VerifyResult verifyResult = jwtUtil.verify(refreshToken);

        // 전달받은 refresh 토큰이 유효하지 않음
        if (!verifyResult.status().equals(TokenStatus.SUCCESS)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = (User) loadUserByUsername(verifyResult.username());

        // 전달받은 refresh 토큰이 저장된 토큰과 일치하지 않음
        if (!tokenService.isRefreshTokenValid(user.getUsername(), refreshToken)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_REFRESH_TOKEN);
        }

        String reissuedAccessToken = jwtUtil.makeAccessToken(user);
        String reissuedRefreshToken = jwtUtil.makeRefreshToken(user);
        tokenService.saveRefreshTokenToRedis(user.getEmail(), reissuedRefreshToken, jwtUtil.getRefreshTime());

        return new TokenDto(reissuedAccessToken, reissuedRefreshToken);
    }

}
