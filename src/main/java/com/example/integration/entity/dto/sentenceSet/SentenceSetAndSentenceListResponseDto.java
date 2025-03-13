package com.example.integration.entity.dto.sentenceSet;

import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import com.example.integration.entity.dto.sentence.SentenceResponseDto;
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
