package com.example.integration.service;

import com.example.api.response.CustomException;
import com.example.integration.config.event.SentenceSetViewedEvent;
import com.example.integration.config.exception.ErrorCode;
import com.example.integration.config.util.SecurityUtil;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import com.example.integration.entity.User;
import com.example.integration.entity.dto.sentence.PagingResponseDto;
import com.example.integration.entity.dto.sentence.SentenceResponseDto;
import com.example.integration.entity.dto.sentenceSet.SentenceSetAndPagingResponseDto;
import com.example.integration.entity.dto.sentenceSet.SentenceSetRequestDto;
import com.example.integration.entity.dto.sentenceSet.SentenceSetResponseDto;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.SentenceSetRepository;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    private SentenceSet findSentenceSetWithId(Long sentenceSetId) {
        return sentenceSetRepository.findById(sentenceSetId)
                .orElseThrow(() -> new CustomException(ErrorCode.SENTENCE_SET_NOT_FOUND));
    }

    /**
     * 특정 문장 세트와 포함된 문장 목록 조회 API
     * @param sentenceSetId
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public SentenceSetAndPagingResponseDto getSentenceSetWithSentences(Long sentenceSetId, int page) {
        User user = currentUser();
        SentenceSet sentenceSet = findSentenceSetWithId(sentenceSetId);

        // 공용 조회가 아닐 때
        if (!sentenceSet.isPublic()) {
            if (user.getId() != sentenceSet.getUser().getId()) {
                throw new CustomException(ErrorCode.PRIVATE_SENTENCE_SET);
            }
        }

        // 문장 조회 후 최근 조회 이벤트 발생
        eventPublisher.publishEvent(new SentenceSetViewedEvent(sentenceSetId));

        // 10개 페이징
        Pageable pageable = PageRequest.of(page, 10);
        Page<Sentence> sentencesPage = sentenceRepository.findBySentenceSetId(sentenceSet.getId(), pageable);

        SentenceSetResponseDto sentenceSetResponseDto = new SentenceSetResponseDto(sentenceSet);

        Page<SentenceResponseDto> sentenceResponseDtoPage = sentencesPage.map(SentenceResponseDto::new);

        PagingResponseDto<SentenceResponseDto> pagingResponseDto = PagingResponseDto.of(sentenceResponseDtoPage);

        return new SentenceSetAndPagingResponseDto(sentenceSetResponseDto, pagingResponseDto);
    }

    /**
     * 사용자의 문장 세트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public List<SentenceSetResponseDto> getSentenceSetByUser() {
        User user = currentUser();
        List<SentenceSet> sentenceSetList = sentenceSetRepository.findAllByUserId(user.getId());

        return sentenceSetList.stream()
                .map(SentenceSetResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 문장 세트 생성
     * @param sentenceSetRequestDto
     * @return
     */
    @Transactional
    public SentenceSetResponseDto createSentenceSet(SentenceSetRequestDto sentenceSetRequestDto) {
        User user = currentUser();

        SentenceSet sentenceSet = SentenceSet.builder()
                .title(sentenceSetRequestDto.title())
                .description(sentenceSetRequestDto.description())
                .isPublic(sentenceSetRequestDto.isPublic())
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

        sentenceSet.updateSentenceSet(sentenceSetRequestDto.title(), sentenceSetRequestDto.description(), sentenceSetRequestDto.isPublic());

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
