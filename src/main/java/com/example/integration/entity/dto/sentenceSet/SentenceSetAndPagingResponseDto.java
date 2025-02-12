package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.dto.sentence.PagingResponseDto;
import com.example.integration.entity.dto.sentence.SentenceResponseDto;

public record SentenceSetAndPagingResponseDto (
        SentenceSetResponseDto sentenceSetResponseDto,
        PagingResponseDto<SentenceResponseDto> pagingResponseDto
) {
}
