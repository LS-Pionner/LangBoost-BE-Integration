package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.SentenceSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ListSentenceSetResponseDto {
    private int sentenceSetCount;
    private List<PublicSentenceSetResponseDto> sentenceSetList;

    public ListSentenceSetResponseDto(List<SentenceSet> sentenceSetList) {
        this.sentenceSetCount = sentenceSetList.size();
        this.sentenceSetList = sentenceSetList.stream()
                .map(PublicSentenceSetResponseDto::new) // 메서드 참조 사용
                .collect(Collectors.toList());
        }
}

