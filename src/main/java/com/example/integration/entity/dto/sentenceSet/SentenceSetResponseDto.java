package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.SentenceSet;

import java.time.LocalDate;

public record SentenceSetResponseDto (
        Long id,
        String title,
        String description,
        boolean isPublic,
        LocalDate lastViewedAt
) {
    public SentenceSetResponseDto(SentenceSet sentenceSet) {
        this(
                sentenceSet.getId(),
                sentenceSet.getTitle(),
                sentenceSet.getDescription(),
                sentenceSet.isPublic(),
                sentenceSet.getLastViewedDate()
        );
    }

}
