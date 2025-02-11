package com.example.integration.controller;

import com.example.api.response.ApiResponse;
import com.example.integration.entity.dto.sentence.PagingResponseDto;
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
     * 특정 문장 조회 API
     * @param sentenceId
     * @return
     */
    @GetMapping("/sentence/{sentenceId}")
    public ApiResponse<SentenceResponseDto> getSentenceById(@PathVariable Long sentenceId) {
        SentenceResponseDto sentence = sentenceService.getSentenceById(sentenceId);

        return ApiResponse.ok(sentence);
    }

    /**
     * 문장 생성 API
     * @param requestDto
     * @return
     */
    @PostMapping("/sentence")
    public ApiResponse<SentenceResponseDto> createSentence(@RequestBody SentenceRequestDto requestDto) {
        SentenceResponseDto createdSentence = sentenceService.createSentence(requestDto);

        return ApiResponse.created(createdSentence);
    }

    /**
     * 특정 문장 수정 API
     * @param sentenceId
     * @param requestDto
     * @return
     */
    @PutMapping("/sentence/{sentenceId}")
    public ApiResponse<SentenceResponseDto> updateSentence(
            @PathVariable Long sentenceId,
            @RequestBody SentenceRequestDto requestDto) {
        SentenceResponseDto updatedSentence = sentenceService.updateSentence(sentenceId, requestDto);

        return ApiResponse.ok(updatedSentence);
    }

    /**
     * 특정 문장 삭제 API
     * @param sentenceId
     * @return
     */
    @DeleteMapping("/sentence/{sentenceId}")
    public ApiResponse<String> deleteSentence(@PathVariable Long sentenceId) {
        sentenceService.deleteSentence(sentenceId);

        return ApiResponse.ok("문장 삭제 완료");
    }

    /**
     * 문장 목록 페이징 조회 API
     * @param page
     * @return
     */
    @GetMapping("/sentences")
    public ApiResponse<PagingResponseDto<SentenceResponseDto>> getSentences(@RequestParam(defaultValue = "1") int page) {
        PagingResponseDto<SentenceResponseDto> pagingResponse = sentenceService.getSentencesPage(page);

        return ApiResponse.ok(pagingResponse);
    }
}
