package com.example.integration.controller;

import com.example.integration.dto.user.*;
import com.example.integration.response.ApiResponse;
import com.example.integration.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입 API
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserRegisterDto userRegisterDto) {
        userService.createUser(userRegisterDto);

        return ApiResponse.ok("회원가입 성공");
    }

    /**
     * 이메일 중복 체크 API
     * @param email
     * @return
     */
    @GetMapping("/email-check")
    public ApiResponse<String> checkEmailAvailable(@RequestParam(name = "email") String email) {
        userService.checkEmailExists(email);

        return ApiResponse.ok("사용가능한 이메일입니다.");
    }

    /**
     * 로그아웃 API
     * @param response
     * @return
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletResponse response) {
        userService.logoutUser();
//        response.addHeader("Set-Cookie", "RefreshToken=; Max-Age=0; path=/; SameSite=Lax"); // 브라우저에 저장된 쿠키 삭제

        Cookie refreshTokenCookie = new Cookie("RefreshToken", "");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        Cookie isAdminCookie = new Cookie("isAdmin", "");
        isAdminCookie.setMaxAge(0);
        isAdminCookie.setPath("/");
        response.addCookie(isAdminCookie);

        return ApiResponse.ok("로그아웃 성공");
    }

    /**
     * 로그인 API
     * @param loginForm
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ApiResponse<UserInfoDto> login(@RequestBody UserLoginForm loginForm, HttpServletResponse response) {
        UserInfoAndTokenDto userInfoAndTokenDto = userService.loginUser(loginForm);

        // RefreshToken 쿠키 설정
        Cookie refreshTokenCookie = new Cookie("RefreshToken", userInfoAndTokenDto.tokenDto().refreshToken());
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(false);
        response.addCookie(refreshTokenCookie);

        // 관리자 여부 쿠키 설정
        Cookie isAdminCookie = new Cookie("isAdmin", String.valueOf(userInfoAndTokenDto.userInfoDto().isAdmin()));
        isAdminCookie.setMaxAge(60 * 60 * 24 * 7);
        isAdminCookie.setPath("/");
        isAdminCookie.setHttpOnly(false);
        response.addCookie(isAdminCookie);

//        ResponseCookie adminCookie = ResponseCookie
//                .from("isAdmin", String.valueOf(userInfoAndTokenDto.userInfoDto().isAdmin()))
//                .maxAge(60 * 60 * 24 * 7)
//                .path("/")
//                .sameSite("Lax")
//                .httpOnly(false)
//                .build();
//
//
//        response.setHeader("Set-Cookie", cookie.toString());

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userInfoAndTokenDto.tokenDto().accessToken());

        return ApiResponse.ok(userInfoAndTokenDto.userInfoDto());
    }

    /**
     * 토큰 재발급 API
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/reissue")
    public ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("RefreshToken");

        TokenDto tokenDto = userService.reissueToken(refreshToken);

        ResponseCookie cookie = ResponseCookie
                .from("RefreshToken", tokenDto.refreshToken())
                .maxAge(60 * 60 * 24 * 7)   // 7일
                .path("/")
                .sameSite("Lax")
                .httpOnly(false)    // 브라우저에서 토큰 접근을 위함
                .build();

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.accessToken());
        response.setHeader("Set-Cookie", cookie.toString());

        return ApiResponse.ok("재발급 성공");
    }
}
