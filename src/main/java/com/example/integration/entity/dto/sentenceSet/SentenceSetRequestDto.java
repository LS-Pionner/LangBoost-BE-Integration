package com.example.integration.entity.dto.sentenceSet;

public record SentenceSetRequestDto (
        String title,
        String description,
        boolean isPublic
) {
}
