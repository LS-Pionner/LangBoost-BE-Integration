package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.SentenceSet;

import java.time.LocalDate;

public record PublicSentenceSetResponseDto (
        Long id,
        String title,
        int sentenceCount,
        LocalDate createdAt
) {
    public PublicSentenceSetResponseDto(SentenceSet sentenceSet) {
        this(
                sentenceSet.getId(),
                sentenceSet.getTitle(),
                sentenceSet.getSentenceList().size(),
                sentenceSet.getCreatedDate()
        );
    }

}

