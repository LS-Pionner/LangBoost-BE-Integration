package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.SentenceSet;

import java.time.LocalDate;

public record SentenceSetResponseDto (
        Long id,
        String title,
        String description,
        int sentenceCount,
        boolean isPublic,
        LocalDate lastViewedAt
) {
    public SentenceSetResponseDto(SentenceSet sentenceSet) {
        this(
                sentenceSet.getId(),
                sentenceSet.getTitle(),
                sentenceSet.getDescription(),
                sentenceSet.getSentenceList().size(),
                sentenceSet.isPublic(),
                sentenceSet.getLastViewedDate()
        );
    }

}
