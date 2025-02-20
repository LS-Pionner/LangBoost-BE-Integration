package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.SentenceSet;
import com.example.integration.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public record UserSentenceSetResponseDto(
        int sentenceSetCount,
        List<SentenceSetResponseDto> sentenceSetResponseDtoList
) {
    public UserSentenceSetResponseDto(User user, List<SentenceSet> sentenceSetList) {
        this(
                user.getSentenceSetList().size(),
                sentenceSetList.stream()
                        .map(sentenceSet -> new SentenceSetResponseDto(sentenceSet))
                        .collect(Collectors.toList())
        );
    }

}