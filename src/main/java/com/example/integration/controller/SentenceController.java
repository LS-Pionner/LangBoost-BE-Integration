package com.example.integration.controller;

import com.example.api.response.ApiResponse;
import com.example.integration.entity.dto.sentence.LearningStatusRequestDto;
import com.example.integration.entity.dto.sentence.SentenceRequestDto;
import com.example.integration.entity.dto.sentence.SentenceResponseDto;
import com.example.integration.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SentenceController {

    private final SentenceService sentenceService;
    /**
     * 문장 세트에 새로운 문장 생성 API
     * @param sentenceSetId
     * @param requestDto
     * @return
     */
    @PostMapping("/sentence-set/{sentenceSetId}/sentence")
    public ApiResponse<SentenceResponseDto> createSentence(@PathVariable Long sentenceSetId,
                                                           @RequestBody SentenceRequestDto requestDto) {
        SentenceResponseDto createdSentence = sentenceService.createSentence(sentenceSetId, requestDto);

        return ApiResponse.created(createdSentence);
    }

    /**
     * 문장 세트에서 특정 문장 수정 API
     * @param sentenceId
     * @param requestDto
     * @return
     */
    @PutMapping("/sentence-set/sentence/{sentenceId}")
    public ApiResponse<SentenceResponseDto> updateSentence(
            @PathVariable Long sentenceId,
            @RequestBody SentenceRequestDto requestDto) {
        SentenceResponseDto updatedSentence = sentenceService.updateSentence(sentenceId, requestDto);

        return ApiResponse.ok(updatedSentence);
    }

    /**
     * 특정 문장의 학습 상태 수정 API
     * @param sentenceId
     * @param learningStatusRequestDto
     * @return
     */
    @PutMapping("/sentence-set/sentence/{sentenceId}/learning-status")
    public ApiResponse<SentenceResponseDto> updateSentenceLearningStatus(
            @PathVariable Long sentenceId,
            @RequestBody LearningStatusRequestDto learningStatusRequestDto) {
        SentenceResponseDto updatedSentence = sentenceService.updateLearningStatus(sentenceId, learningStatusRequestDto);

        return ApiResponse.ok(updatedSentence);
    }

    /**
     * 문장 세트에서 특정 문장 삭제 API
     * @param sentenceId
     * @return
     */
    @DeleteMapping("/sentence-set/sentence/{sentenceId}")
    public ApiResponse<String> deleteSentence(@PathVariable Long sentenceId) {
        sentenceService.deleteSentence(sentenceId);

        return ApiResponse.ok("문장 세트에서 문장 삭제 완료");
    }

}
