package com.example.integration.config.event;

import com.example.integration.config.util.DateUtil;
import com.example.integration.repository.SentenceSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
public class SentenceSetViewedEventListener {

    private final SentenceSetRepository sentenceSetRepository;

    @Async
    @EventListener
    @Transactional
    // 문장 세트 조회 이벤트가 발생한다면 현재 날짜로 업데이트
    public void handleSentenceSetViewedEvent(SentenceSetViewedEvent event) {
        sentenceSetRepository.findById(event.getSentenceSetId()).ifPresent(sentenceSet -> {
            sentenceSet.updateLastViewedDate(DateUtil.getLastViewedDate());
            sentenceSetRepository.save(sentenceSet);
        });
    }
}
