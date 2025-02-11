package com.example.integration.service;

import com.example.api.response.CustomException;
import com.example.integration.config.util.SecurityUtil;
import com.example.integration.config.event.SentenceViewedEvent;
import com.example.integration.config.exception.ErrorCode;
import com.example.integration.config.util.DateUtil;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.User;
import com.example.integration.entity.dto.sentence.PagingResponseDto;
import com.example.integration.entity.dto.sentence.SentenceRequestDto;
import com.example.integration.entity.dto.sentence.SentenceResponseDto;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SentenceService {

    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 현재 사용자 조회
     * @return
     */
    @Transactional(readOnly = true)
    private User currentUser() {
        return userRepository.findByEmail(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    /**
     * 특정 문장 조회
     * @param sentenceId
     * @return
     */
    @Transactional(readOnly = true)
    public SentenceResponseDto getSentenceById(Long sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND_SENTENCE));

        // 문장 조회 후 최근 조회 이벤트 발생
        eventPublisher.publishEvent(new SentenceViewedEvent(sentenceId));

        return new SentenceResponseDto(sentence);
    }

    /**
     * 새로운 문장 생성
     * @param requestDto
     * @return
     */
    @Transactional
    public SentenceResponseDto createSentence(SentenceRequestDto requestDto) {
        User user = currentUser();

        Sentence sentence = Sentence.builder()
                .sentence(requestDto.sentence())
                .description(requestDto.description())
                .lastViewedDate(DateUtil.getLastViewedDate())
                .user(user)
                .build();

        Sentence savedSentence = sentenceRepository.save(sentence);

        return new SentenceResponseDto(savedSentence);
    }

    /**
     * 기존 문장 수정
     * @param sentenceId
     * @param requestDto
     * @return
     */
    @Transactional
    public SentenceResponseDto updateSentence(Long sentenceId, SentenceRequestDto requestDto) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND_SENTENCE));

        sentence.updateSentence(requestDto.sentence(), requestDto.description());

        Sentence updatedSentence = sentenceRepository.save(sentence);

        return new SentenceResponseDto(updatedSentence);
    }

    /**
     * 기존 문장 삭제
     * @param sentenceId
     */
    @Transactional
    public void deleteSentence(Long sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND_SENTENCE));

        sentenceRepository.delete(sentence);
    }

    // sentence 페이징
    @Transactional(readOnly = true)
    public PagingResponseDto<SentenceResponseDto> getSentencesPage(int page) {
        User user = currentUser();

        // 10개 페이징
        Pageable pageable = PageRequest.of(page, 10);

        Page<Sentence> sentencePage = sentenceRepository.findByUserId(user.getId(), pageable);
        Page<SentenceResponseDto> sentencesPage = sentencePage.map(SentenceResponseDto::new);

        return PagingResponseDto.of(sentencesPage);
    }
}
