package com.example.integration.service;

import com.example.integration.common.config.event.SentenceSetViewedEvent;
import com.example.integration.response.CustomException;
import com.example.integration.response.ErrorCode;
import com.example.integration.common.util.SecurityUtil;
import com.example.integration.dto.sentenceSet.*;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import com.example.integration.entity.User;
import com.example.integration.dto.sentence.PagingResponseDto;
import com.example.integration.dto.sentence.PublicSentenceResponseDto;
import com.example.integration.dto.sentence.SentenceResponseDto;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.SentenceSetRepository;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SentenceSetService {

    private final SentenceSetRepository sentenceSetRepository;
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 현재 사용자 조회
     * @return
     */
    private User currentUser() {
        return userRepository.findByEmail(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    /**
     * id를 통해 특정 문장 세트 조회
     * @param sentenceSetId
     * @return
     */
    private SentenceSet findSentenceSetWithId(Long sentenceSetId) {
        return sentenceSetRepository.findById(sentenceSetId)
                .orElseThrow(() -> new CustomException(ErrorCode.SENTENCE_SET_NOT_FOUND));
    }

    /**
     * 개인 문장 세트 조회
     * @param offset
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public UserSentenceSetResponseDto getSentenceSetByUser(int offset, int limit) {
        User user = currentUser();
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<SentenceSet> sentenceSetList = sentenceSetRepository.findAllByUserId(user.getId(), pageable);

        return new UserSentenceSetResponseDto(user, sentenceSetList);
    }

    /**
     * 특정 개인 문장 세트와 포함된 문장 목록 조회
     * @param sentenceSetId
     * @param offset
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public SentenceSetAndSentenceListResponseDto getSentenceSetWithSentences(Long sentenceSetId, int offset, int limit) {
        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        // 개인 문장 세트 조회일 때
        if (!sentenceSet.isPublic()) {
            User user = currentUser();

            if (user.getId() == sentenceSet.getUser().getId()) {
                // 문장 조회 후 최근 조회 이벤트 발생
                eventPublisher.publishEvent(new SentenceSetViewedEvent(sentenceSetId));
            } else {
                throw new CustomException(ErrorCode.PRIVATE_SENTENCE_SET);
            }
        }

        // 10개 페이징
        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<Sentence> sentenceList = sentenceRepository.findAllBySentenceSetId(sentenceSetId, pageable);

        return new SentenceSetAndSentenceListResponseDto(sentenceSet, sentenceList);
    }

    /**
     * 새로운 개인 문장 세트 생성
     * @param sentenceSetRequestDto
     * @return
     */
    @Transactional
    public SentenceSetResponseDto createSentenceSet(SentenceSetRequestDto sentenceSetRequestDto) {
        User user = currentUser();

        SentenceSet sentenceSet = SentenceSet.builder()
                .title(sentenceSetRequestDto.title())
                .isPublic(false)
                .user(user)
                .build();

        SentenceSet savedSentenceSet = sentenceSetRepository.save(sentenceSet);

        return new SentenceSetResponseDto(savedSentenceSet);
    }

    /**
     * 기존 문장 세트 수정
     * @param sentenceSetId
     * @param sentenceSetRequestDto
     * @return
     */
    @Transactional
    public SentenceSetResponseDto updateSentenceSet(Long sentenceSetId, SentenceSetRequestDto sentenceSetRequestDto) {
        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        sentenceSet.updateSentenceSet(sentenceSetRequestDto.title());

        SentenceSet updatedSentenceSet = sentenceSetRepository.save(sentenceSet);

        return new SentenceSetResponseDto(updatedSentenceSet);
    }

    /**
     * 기존 문장 세트 삭제
     * @param sentenceSetId
     */
    @Transactional
    public void deleteSentenceSet(Long sentenceSetId) {
        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        sentenceSetRepository.delete(sentenceSet);
    }

}
