package com.example.integration.entity.dto.sentence;

import com.example.integration.entity.LearningStatus;
import com.example.integration.entity.Sentence;

public record PublicSentenceResponseDto(
        Long id,
        String content,
        String meaning,
        String description,
        String learningStatus
) {
    public PublicSentenceResponseDto(Sentence sentence) {
        this(
                sentence.getId(),
                sentence.getContent(),
                sentence.getMeaning(),
                sentence.getDescription(),
                sentence.getLearningStatus().getDescription()
        );
    }

}

