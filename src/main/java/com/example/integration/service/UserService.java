package com.example.integration.service;

import com.example.integration.response.CustomException;
import com.example.integration.response.ErrorCode;
import com.example.integration.common.config.jwt.JWTUtil;
import com.example.integration.common.config.jwt.TokenStatus;
import com.example.integration.common.util.SecurityUtil;
import com.example.integration.dto.user.*;
import com.example.integration.entity.RoleType;
import com.example.integration.entity.User;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
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
     * 현재 사용자 조회
     * @return User 객체
     */
    @Transactional(readOnly = true)
    public User currentUser() {
        return userRepository.findByEmail(SecurityUtil.getCurrentMember()).orElseThrow(
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
        try {
            // 사용자 이름으로 로드
            loadUserByUsername(loginForm.getUsername());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증된 사용자 정보 가져오기
            User user = (User) authentication.getPrincipal();

            // 액세스 토큰과 리프레시 토큰 생성
            String accessToken = jwtUtil.makeAccessToken(user);
            String refreshToken = jwtUtil.makeRefreshToken(user);

            // 리프레시 토큰을 Redis에 저장
            tokenService.saveRefreshTokenToRedis(user.getEmail(), refreshToken, jwtUtil.getRefreshTime());

            // 응답할 토큰 DTO와 사용자 정보 DTO 생성
            TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
            UserInfoDto userInfoDto = new UserInfoDto(user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), user.isEnabled());

            return new UserInfoAndTokenDto(userInfoDto, tokenDto);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOGIN_ERROR);
        }
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

    /**
     * 비밀번호 변경
     * @param passwordChangeDto
     */
    @Transactional
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        User user = currentUser();

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(passwordChangeDto.currentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);  // 현재 비밀번호가 틀린 경우 예외 처리
        }

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(passwordChangeDto.newPassword());

        // 새로운 비밀번호로 업데이트
        User updatedUser = user.toBuilder()
                .password(encodedNewPassword)
                .build();

        userRepository.save(updatedUser);
    }



}
