package com.example.integration.entity.dto.sentence;

public record SentenceRequestDto(
        String content,
        String meaning,
        String description
) {
}
