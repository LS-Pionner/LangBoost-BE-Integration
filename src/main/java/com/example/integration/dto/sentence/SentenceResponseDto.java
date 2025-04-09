package com.example.integration.dto.sentence;

import com.example.integration.entity.LearningStatus;
import com.example.integration.entity.Sentence;

public record SentenceResponseDto(
        Long id,
        String content,
        String meaning,
        String description,
        LearningStatus learningStatus
) {
    public SentenceResponseDto(Sentence sentence) {
        this(
                sentence.getId(),
                sentence.getContent(),
                sentence.getMeaning(),
                sentence.getDescription(),
                sentence.getLearningStatus()
        );
    }

}
