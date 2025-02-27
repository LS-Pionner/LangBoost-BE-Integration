package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.SentenceSet;

public record PublicSentenceSetResponseDto (
        Long id,
        String title
) {
    public PublicSentenceSetResponseDto(SentenceSet sentenceSet) {
        this(
                sentenceSet.getId(),
                sentenceSet.getTitle()
        );
    }

}

