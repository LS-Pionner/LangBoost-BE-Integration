package com.example.integration.dto.sentenceSet;

import com.example.integration.entity.LearningStatus;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;

import java.time.LocalDate;

public record SentenceSetResponseDto (
        Long id,
        String title,
        int sentenceCount,
        int inProgressSentenceCount,
        int completedSentenceCount,
        LocalDate createdAt,
        LocalDate lastViewedAt
) {
    public SentenceSetResponseDto(SentenceSet sentenceSet) {
        this(
                sentenceSet.getId(),
                sentenceSet.getTitle(),
                sentenceSet.getSentenceList().size(),
                getSentenceCounts(sentenceSet).inProgressCount,
                getSentenceCounts(sentenceSet).completedCount,
                sentenceSet.getCreatedDate(),
                sentenceSet.getLastViewedDate()
        );
    }

    private static SentenceCounts getSentenceCounts(SentenceSet sentenceSet) {
        int inProgressCount = 0;
        int completedCount = 0;

        for (Sentence sentence : sentenceSet.getSentenceList()) {
            if (LearningStatus.IN_PROGRESS.equals(sentence.getLearningStatus())) {
                inProgressCount++;
            } else {
                completedCount++;
            }
        }

        return new SentenceCounts(inProgressCount, completedCount);
    }

    public record SentenceCounts(int inProgressCount, int completedCount) {}
}
