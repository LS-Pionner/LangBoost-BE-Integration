package com.example.integration.dto.sentenceSet;

import com.example.integration.dto.sentence.PagingResponseDto;
import com.example.integration.dto.sentence.SentenceResponseDto;

public record SentenceSetAndPagingResponseDto (
        SentenceSetResponseDto sentenceSetResponseDto,
        PagingResponseDto<SentenceResponseDto> pagingResponseDto
) {
}
