package com.example.integration.entity.dto.mypage;

public record SentenceLearningStatusesDto(
        long totalSentences,
        long learningStatusInProgress,
        long learningStatusCompleted
) {
}
