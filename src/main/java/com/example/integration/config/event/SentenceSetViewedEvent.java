package com.example.integration.config.event;

import lombok.Getter;

@Getter
public class SentenceSetViewedEvent {

    private final Long sentenceSetId;

    public SentenceSetViewedEvent(Long sentenceSetId) {
        this.sentenceSetId = sentenceSetId;
    }

}
