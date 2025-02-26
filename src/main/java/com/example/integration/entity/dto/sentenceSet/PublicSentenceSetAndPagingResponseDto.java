package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.dto.sentence.PagingResponseDto;
import com.example.integration.entity.dto.sentence.PublicSentenceResponseDto;

public record PublicSentenceSetAndPagingResponseDto (
        PublicSentenceSetResponseDto sentenceSetResponseDto,
        PagingResponseDto<PublicSentenceResponseDto> pagingResponseDto
) {
}