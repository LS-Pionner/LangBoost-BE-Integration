package com.example.integration.controller;

import com.example.api.response.ApiResponse;
import com.example.integration.entity.dto.sentenceSet.*;
import com.example.integration.service.SentenceSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SentenceSetController {

    private final SentenceSetService sentenceSetService;

    /**
     * 개인 문장 세트 목록 조회 API
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/sentence-set")
    public ApiResponse<UserSentenceSetResponseDto> getSentenceSetById(@RequestParam(defaultValue = "0") int offset,
                                                                      @RequestParam(defaultValue = "10") int limit) {
        UserSentenceSetResponseDto userSentenceSetResponseDto = sentenceSetService.getSentenceSetByUser(offset, limit);

        return ApiResponse.ok(userSentenceSetResponseDto);
    }

    /**
     * 특정 개인 문장 세트와 포함된 문장 목록 조회 API
     * @param sentenceSetId
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/sentence-set/{sentenceSetId}")
    public ApiResponse<SentenceSetAndSentenceListResponseDto> getSentenceSetWithSentences(@PathVariable Long sentenceSetId,
                                                                                          @RequestParam(defaultValue = "0") int offset,
                                                                                          @RequestParam(defaultValue = "10") int limit) {
        SentenceSetAndSentenceListResponseDto sentenceSetWithSentences = sentenceSetService.getSentenceSetWithSentences(sentenceSetId, offset, limit);

        return ApiResponse.ok(sentenceSetWithSentences);
    }

    /**
     * 새로운 개인 문장 세트 생성 API
     * @param sentenceSetRequestDto
     * @return
     */
    @PostMapping("/sentence-set")
    public ApiResponse<SentenceSetResponseDto> createSentenceSet(@RequestBody SentenceSetRequestDto sentenceSetRequestDto) {
        SentenceSetResponseDto createdSentenceSet = sentenceSetService.createSentenceSet(sentenceSetRequestDto);

        return ApiResponse.created(createdSentenceSet);
    }

    /**
     * 기존 문장 세트 수정 API
     * @param sentenceSetId
     * @param sentenceSetRequestDto
     * @return
     */
    @PutMapping("/sentence-set/{sentenceSetId}")
    public ApiResponse<SentenceSetResponseDto> updateSentenceSet(@PathVariable Long sentenceSetId,
                                                                 @RequestBody SentenceSetRequestDto sentenceSetRequestDto) {
        SentenceSetResponseDto updatedSentenceSet = sentenceSetService.updateSentenceSet(sentenceSetId, sentenceSetRequestDto);

        return ApiResponse.ok(updatedSentenceSet);
    }

    /**
     * 기존 문장 세트 삭제 API
     * @param sentenceSetId
     * @return
     */
    @DeleteMapping("/sentence-set/{sentenceSetId}")
    public ApiResponse<String> deleteSentenceSet(@PathVariable Long sentenceSetId) {
        sentenceSetService.deleteSentenceSet(sentenceSetId);

        return ApiResponse.ok("문장 세트 삭제 완료");
    }

}
