package com.example.integration.dto.sentenceSet;

import com.example.integration.dto.sentence.SentenceResponseDto;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SentenceSetAndSentenceListResponseDto {
    private SentenceSetResponseDto sentenceSet;
    private List<SentenceResponseDto> sentenceList;

    public SentenceSetAndSentenceListResponseDto(SentenceSet sentenceSet, List<Sentence> sentenceList) {
            this.sentenceSet = new SentenceSetResponseDto(sentenceSet);
            this.sentenceList = sentenceList.stream()
                    .map(sentence -> new SentenceResponseDto(sentence))
                    .collect(Collectors.toList());
    }
}
