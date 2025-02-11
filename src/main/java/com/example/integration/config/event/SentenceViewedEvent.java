package com.example.integration.config.event;

public class SentenceViewedEvent {

    private final Long sentenceId;

    public SentenceViewedEvent(Long sentenceId) {
        this.sentenceId = sentenceId;
    }

    public Long getSentenceId() {
        return sentenceId;
    }

}
