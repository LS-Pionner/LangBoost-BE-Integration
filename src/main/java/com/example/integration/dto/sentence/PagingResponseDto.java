package com.example.integration.dto.sentence;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagingResponseDto<T>(
        int pageNumber,          // 현재 페이지 번호
        int pageSize,            // 페이지 크기
        long totalElements,      // 총 데이터 개수
        int totalPages,          // 총 페이지 수
        boolean isLast,          // 마지막 페이지 여부
        boolean isFirst,         // 첫 번째 페이지 여부
        int numberOfElements,    // 현재 페이지의 데이터 개수
        boolean isEmpty,          // 데이터가 비어 있는지 여부
        List<T> content         // 실제 데이터 리스트
) {
    // Page 객체로부터 PagingResponseDto 생성하는 메서드
    public static <T> PagingResponseDto<T> of(Page<T> page) {
        return new PagingResponseDto<>(
                page.getNumber(),           // 현재 페이지 번호
                page.getSize(),             // 페이지 크기
                page.getTotalElements(),    // 총 데이터 개수
                page.getTotalPages(),       // 총 페이지 수
                page.isLast(),              // 마지막 페이지 여부
                page.isFirst(),             // 첫 번째 페이지 여부
                page.getNumberOfElements(), // 현재 페이지의 데이터 개수
                page.isEmpty(),             // 데이터가 비어 있는지 여부
                page.getContent()          // 실제 데이터 리스트
        );
    }
}
