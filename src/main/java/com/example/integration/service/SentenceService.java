package com.example.integration.service;

import com.example.api.response.CustomException;
import com.example.integration.config.exception.ErrorCode;
import com.example.integration.config.util.SecurityUtil;
import com.example.integration.entity.LearningStatus;
import com.example.integration.entity.Sentence;
import com.example.integration.entity.SentenceSet;
import com.example.integration.entity.User;
import com.example.integration.entity.dto.sentence.LearningStatusRequestDto;
import com.example.integration.entity.dto.sentence.SentenceRequestDto;
import com.example.integration.entity.dto.sentence.SentenceResponseDto;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.SentenceSetRepository;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SentenceService {

    private final SentenceRepository sentenceRepository;
    private final SentenceSetRepository sentenceSetRepository;
    private final UserRepository userRepository;

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
    private SentenceSet findSentenceSet(Long sentenceSetId) {
        return sentenceSetRepository.findById(sentenceSetId)
                .orElseThrow(() -> new CustomException(ErrorCode.SENTENCE_SET_NOT_FOUND));
    }

    /**
     * 현재 로그인 한 사용자와 문장 세트를 작성한 사용자 일치 여부
     * @param sentenceSetId
     */
    private void checkWriter(Long sentenceSetId) {
        if (!sentenceSetRepository.existsByUserIdAndSentenceSetId(currentUser().getId(), sentenceSetId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 문장 세트에 새로운 문장 생성
     * @param sentenceSetId
     * @param requestDto
     * @return
     */
    @Transactional
    public SentenceResponseDto createSentence(Long sentenceSetId, SentenceRequestDto requestDto) {
        SentenceSet sentenceSet = findSentenceSet(sentenceSetId);

        checkWriter(sentenceSetId);

        Sentence sentence = Sentence.builder()
                .content(requestDto.content())
                .meaning(requestDto.meaning())
                .description(requestDto.description())
                .learningStatus(LearningStatus.IN_PROGRESS)
                .sentenceSet(sentenceSet)
                .build();

        Sentence savedSentence = sentenceRepository.save(sentence);

        return new SentenceResponseDto(savedSentence);
    }

    /**
     * 문장 세트에서 특정 문장 수정
     * @param sentenceId
     * @param requestDto
     * @return
     */
    @Transactional
    public SentenceResponseDto updateSentence(Long sentenceId, SentenceRequestDto requestDto) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND_SENTENCE));

        checkWriter(sentence.getSentenceSet().getId());

        sentence.updateSentence(requestDto.content(), requestDto.meaning(), requestDto.description());

        Sentence updatedSentence = sentenceRepository.save(sentence);

        return new SentenceResponseDto(updatedSentence);
    }

    /**
     * 특정 문장의 학습 상태 수정
     * @param sentenceId
     * @param learningStatusRequestDto
     * @return
     */
    @Transactional
    public SentenceResponseDto updateLearningStatus(Long sentenceId, LearningStatusRequestDto learningStatusRequestDto) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND_SENTENCE));

        checkWriter(sentence.getSentenceSet().getId());

        sentence.updateLearningStatus(learningStatusRequestDto.learningStatus());

        Sentence updatedSentence = sentenceRepository.save(sentence);

        return new SentenceResponseDto(updatedSentence);
    }

    /**
     * 문장 세트에서 특정 문장 삭제
     * @param sentenceId
     */
    @Transactional
    public void deleteSentence(Long sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND_SENTENCE));

        checkWriter(sentence.getSentenceSet().getId());

        sentenceRepository.delete(sentence);
    }

}
