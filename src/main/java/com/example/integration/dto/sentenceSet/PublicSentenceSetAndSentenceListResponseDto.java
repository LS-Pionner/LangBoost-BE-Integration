package com.example.integration.dto.sentenceSet;

import com.example.integration.dto.sentence.PublicSentenceResponseDto;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PublicSentenceSetAndSentenceListResponseDto {
    private PublicSentenceSetResponseDto sentenceSet;
    private List<PublicSentenceResponseDto> sentenceList;

    public PublicSentenceSetAndSentenceListResponseDto(SentenceSet sentenceSet, List<Sentence> sentenceList) {
        this.sentenceSet = new PublicSentenceSetResponseDto(sentenceSet);
        this.sentenceList = sentenceList.stream()
                .map(sentence -> new PublicSentenceResponseDto(sentence))
                .collect(Collectors.toList());
    }
}