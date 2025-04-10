package com.example.integration.dto.sentenceSet;

import com.example.integration.dto.sentence.PagingResponseDto;
import com.example.integration.dto.sentence.PublicSentenceResponseDto;

public record PublicSentenceSetAndPagingResponseDto (
        PublicSentenceSetResponseDto sentenceSetResponseDto,
        PagingResponseDto<PublicSentenceResponseDto> pagingResponseDto
) {
}