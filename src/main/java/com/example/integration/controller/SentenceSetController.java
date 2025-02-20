package com.example.integration.controller;

import com.example.api.response.ApiResponse;
import com.example.integration.entity.dto.sentenceSet.*;
import com.example.integration.service.SentenceSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SentenceSetController {

    private final SentenceSetService sentenceSetService;

    /**
     * 문장 세트 목록 조회 API
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/sentence-set")
    public ApiResponse<ListSentenceSetResponseDto> getSentenceSetList(@RequestParam(defaultValue = "0") int offset,
                                                                      @RequestParam(defaultValue = "10") int limit) {
        ListSentenceSetResponseDto listSentenceSetResponseDto = sentenceSetService.getSentenceSetList(offset, limit);

        return ApiResponse.ok(listSentenceSetResponseDto);
    }

    /**
     * 키워드로 문장 세트 목록 검색 API
     * @param keyword
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/sentence-set/search")
    public ApiResponse<ListSentenceSetResponseDto> searchSentenceSetList(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                         @RequestParam(defaultValue = "0") int offset,
                                                                         @RequestParam(defaultValue = "10") int limit) {
        ListSentenceSetResponseDto listSentenceSetResponseDto = sentenceSetService.searchSentenceSetList(keyword, offset, limit);

        return ApiResponse.ok(listSentenceSetResponseDto);
    }

    /**
     * 사용자의 문장 세트 조회 API
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("my/sentence-set")
    public ApiResponse<UserSentenceSetResponseDto> getSentenceSetById(@RequestParam(defaultValue = "0") int offset,
                                                                      @RequestParam(defaultValue = "10") int limit) {
        UserSentenceSetResponseDto userSentenceSetResponseDto = sentenceSetService.getSentenceSetByUser(offset, limit);

        return ApiResponse.ok(userSentenceSetResponseDto);
    }

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
