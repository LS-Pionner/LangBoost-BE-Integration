package com.example.integration.controller;

import com.example.integration.dto.sentenceSet.ListSentenceSetResponseDto;
import com.example.integration.dto.sentenceSet.PublicSentenceSetAndSentenceListResponseDto;
import com.example.integration.dto.sentenceSet.PublicSentenceSetResponseDto;
import com.example.integration.dto.sentenceSet.SentenceSetRequestDto;
import com.example.integration.response.ApiResponse;
import com.example.integration.service.PublicSentenceSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class PublicSentenceSetController {

    private final PublicSentenceSetService publicSentenceSetService;

    /**
     * 공용 문장 세트 목록 조회 API
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/public/sentence-set")
    public ApiResponse<ListSentenceSetResponseDto> getPublicSentenceSetList(@RequestParam(defaultValue = "0") int offset,
                                                                            @RequestParam(defaultValue = "10") int limit) {
        ListSentenceSetResponseDto listSentenceSetResponseDto = publicSentenceSetService.getPublicSentenceSetList(offset, limit);
        log.info("offset: {}", offset);
        return ApiResponse.ok(listSentenceSetResponseDto);
    }

    /**
     * 키워드로 문장 세트 목록 검색 API
     * 현재는 공용 모든 문장 세트에 대해서 검색
     * (추후 공용 여부에 따라 쿼리 수정)
     * @param keyword
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/public/sentence-set/search")
    public ApiResponse<ListSentenceSetResponseDto> searchPublicSentenceSetList(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                               @RequestParam(defaultValue = "0") int offset,
                                                                               @RequestParam(defaultValue = "10") int limit) {
        ListSentenceSetResponseDto listSentenceSetResponseDto = publicSentenceSetService.searchPublicSentenceSetList(keyword, offset, limit);

        return ApiResponse.ok(listSentenceSetResponseDto);
    }

    /**
     * 특정 공용 문장 세트와 포함된 문장 목록 조회 API
     * @param sentenceSetId
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/public/sentence-set/{sentenceSetId}")
    public ApiResponse<PublicSentenceSetAndSentenceListResponseDto> getPublicSentenceSetWithSentences(@PathVariable Long sentenceSetId,
                                                                                                      @RequestParam(defaultValue = "0") int offset,
                                                                                                      @RequestParam(defaultValue = "10") int limit) {
        PublicSentenceSetAndSentenceListResponseDto sentenceSetWithSentences = publicSentenceSetService.getPublicSentenceSetWithSentences(sentenceSetId, offset, limit);

        return ApiResponse.ok(sentenceSetWithSentences);
    }

    /**
     * 새로운 공용 문장 세트 생성 API (관리자)
     * @param sentenceSetRequestDto
     * @return
     */
    @PostMapping("/admin/sentence-set")
    public ApiResponse<PublicSentenceSetResponseDto> createPublicSentenceSet(@RequestBody SentenceSetRequestDto sentenceSetRequestDto) {
        PublicSentenceSetResponseDto createdSentenceSet = publicSentenceSetService.createPublicSentenceSet(sentenceSetRequestDto);

        return ApiResponse.created(createdSentenceSet);
    }

    /**
     * 기존 공용 문장 세트 수정 API (관리자)
     * @param sentenceSetId
     * @param sentenceSetRequestDto
     * @return
     */
    @PutMapping("/admin/sentence-set/{sentenceSetId}")
    public ApiResponse<PublicSentenceSetResponseDto> updateSentenceSet(@PathVariable Long sentenceSetId,
                                                                 @RequestBody SentenceSetRequestDto sentenceSetRequestDto) {
        PublicSentenceSetResponseDto updatedSentenceSet = publicSentenceSetService.updateSentenceSet(sentenceSetId, sentenceSetRequestDto);

        return ApiResponse.ok(updatedSentenceSet);
    }

    /**
     * 기존 공용 문장 세트 삭제 API (관리자)
     * @param sentenceSetId
     * @return
     */
    @DeleteMapping("/admin/sentence-set/{sentenceSetId}")
    public ApiResponse<String> deleteSentenceSet(@PathVariable Long sentenceSetId) {
        publicSentenceSetService.deleteSentenceSet(sentenceSetId);

        return ApiResponse.ok("문장 세트 삭제 완료");
    }

}
