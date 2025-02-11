package com.example.integration.entity.dto.sentence;

import com.example.integration.entity.Sentence;

import java.time.LocalDate;

public record SentenceResponseDto(
        Long id,
        String sentence,
        String description,
        LocalDate lastViewedDate
) {
    public SentenceResponseDto(Sentence sentence) {
        this(
                sentence.getId(),
                sentence.getSentence(),
                sentence.getDescription(),
                sentence.getLastViewedDate()
        );
    }
}
