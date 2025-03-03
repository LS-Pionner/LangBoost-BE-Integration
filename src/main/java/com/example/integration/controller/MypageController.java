package com.example.integration.controller;

import com.example.api.response.ApiResponse;
import com.example.integration.entity.dto.mypage.SentenceLearningStatusesDto;
import com.example.integration.service.MypageService;
import com.example.integration.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class MypageController {
    private final MypageService mypageService;


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

}

