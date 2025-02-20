package com.example.integration.controller;

import com.example.api.response.ApiResponse;
import com.example.integration.entity.dto.sentenceSet.SentenceSetAndPagingResponseDto;
import com.example.integration.entity.dto.sentenceSet.SentenceSetRequestDto;
import com.example.integration.entity.dto.sentenceSet.SentenceSetResponseDto;
import com.example.integration.entity.dto.sentenceSet.UserSentenceSetResponseDto;
import com.example.integration.service.SentenceSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SentenceSetController {

    private final SentenceSetService sentenceSetService;

    /**
     * 특정 문장 세트와 포함된 문장 목록 조회 API
     * @param sentenceSetId
     * @param page
     * @return
     */
    @GetMapping("/sentence-set/{sentenceSetId}")
    public ApiResponse<SentenceSetAndPagingResponseDto> getSentenceSetWithSentences(@PathVariable Long sentenceSetId,
                                                                                    @RequestParam(defaultValue = "0") int page) {
        SentenceSetAndPagingResponseDto sentenceSetWithSentences = sentenceSetService.getSentenceSetWithSentences(sentenceSetId, page);

        return ApiResponse.ok(sentenceSetWithSentences);
    }

    /**
     * 사용자의 문장 세트 조회 API
     * @return
     */
    @GetMapping("my/sentence-set")
    public ApiResponse<UserSentenceSetResponseDto> getSentenceSetById() {
        UserSentenceSetResponseDto userSentenceSetResponseDto = sentenceSetService.getSentenceSetByUser();

        return ApiResponse.ok(userSentenceSetResponseDto);
    }

    /**
     * 새로운 문장 세트 생성 API
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
