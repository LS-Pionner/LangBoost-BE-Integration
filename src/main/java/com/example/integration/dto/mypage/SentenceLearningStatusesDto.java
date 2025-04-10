package com.example.integration.dto.mypage;

public record SentenceLearningStatusesDto(
        long totalSentences,
        long learningStatusInProgress,
        long learningStatusCompleted
) {
}
