package com.example.integration.service;

import com.example.api.response.CustomException;
import com.example.integration.config.exception.ErrorCode;
import com.example.integration.config.util.SecurityUtil;
import com.example.integration.entity.User;
import com.example.integration.entity.dto.mypage.SentenceLearningStatusesDto;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.SentenceSetRepository;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final SentenceSetRepository sentenceSetRepository;
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;

    /**
     * 현재 사용자 조회
     * @return User 객체
     */
    @Transactional(readOnly = true)
    private User currentUser() {
        return userRepository.findByEmail(SecurityUtil.getCurrentMember()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    /**
     * 현재 사용자의 SentenceSet 개수 조회
     * @return 현재 사용자의 SentenceSet 개수
     */
    @Transactional(readOnly = true)
    public long getUserSentenceSetCount() {
        User user = currentUser();
        return sentenceSetRepository.countByUser(user.getId());
    }

    /**
     * 현재 사용자의 문장 통계 (총 문장 개수, 학습 중인 문장, 학습 완료한 문장 개수)를 반환하는 메서드
     * @return 문장 통계
     */
    @Transactional(readOnly = true)
    public SentenceLearningStatusesDto getUserSentenceStatistics() {
        User user = currentUser();

        long totalSentences = sentenceRepository.countByUserId(user.getId());
        long learningStatusInProgress = sentenceRepository.countByUserIdAndLearningStatusInProgress(user.getId());
        long learningStatusCompleted = sentenceRepository.countByUserIdAndLearningStatusCompleted(user.getId());

        return new SentenceLearningStatusesDto(totalSentences, learningStatusInProgress, learningStatusCompleted);
    }



}
