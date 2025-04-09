package com.example.integration.dto.sentence;

public record SentenceRequestDto(
        String content,
        String meaning,
        String description
) {
}
