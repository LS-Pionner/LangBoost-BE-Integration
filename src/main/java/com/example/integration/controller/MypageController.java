package com.example.integration.controller;

import com.example.integration.dto.mypage.SentenceLearningStatusesDto;
import com.example.integration.dto.user.PasswordChangeDto;
import com.example.integration.common.response.ApiResponse;
import com.example.integration.service.MypageService;
import com.example.integration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class MypageController {
    private final MypageService mypageService;
    private final UserService userService;

    /**
     * 현재 사용자의 이메일을 반환하는 API
     * @return 사용자의 이메일
     */
    @GetMapping("/user/email")
    public ApiResponse<String> getUserEmail() {
        String email = mypageService.getCurrentUserEmail();
        return ApiResponse.ok(email);
    }

    /**
     * 현재 사용자의 문장 세트 개수를 반환하는 API
     * @return 사용자의 문장 세트 개수
     */
    @GetMapping("/sentence-sets/count")
    public ApiResponse<Long> getUserSentenceSetCount() {
        long count = mypageService.getUserSentenceSetCount();
        return ApiResponse.ok(count);
    }

    /**
     * 현재 사용자의 문장 통계 (총 문장 개수, 학습 중인 문장, 학습 완료한 문장 개수)를 반환하는 API
     * @return 문장 통계
     */
    @GetMapping("/sentences/statistics")
    public ApiResponse<SentenceLearningStatusesDto> getUserSentenceStatistics() {
        SentenceLearningStatusesDto sentenceStatistics = mypageService.getUserSentenceStatistics();
        return ApiResponse.ok(sentenceStatistics);
    }

    /**
     * 비밀번호 변경 API
     * @param passwordChangeDto 현재 비밀번호와 새 비밀번호를 담고 있는 DTO
     * @return 성공 메시지
     */
    @PostMapping("/user/password")
    public ApiResponse<String> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto);
        return ApiResponse.ok("Password changed successfully");
    }

}

