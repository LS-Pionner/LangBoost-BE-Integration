package com.example.integration.service;

import com.example.integration.entity.User;
import com.example.integration.dto.mypage.SentenceLearningStatusesDto;
import com.example.integration.repository.SentenceRepository;
import com.example.integration.repository.SentenceSetRepository;
import com.example.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserService userService;
    private final SentenceSetRepository sentenceSetRepository;
    private final SentenceRepository sentenceRepository;
    private final UserRepository userRepository;


    /**
     * 현재 사용자의 이메일을 반환하는 메서드
     * @return 현재 사용자의 이메일
     */
    @Transactional(readOnly = true)
    public String getCurrentUserEmail() {
        return userService.currentUser().getEmail();
    }


    /**
     * 현재 사용자의 SentenceSet 개수 조회
     * @return 현재 사용자의 SentenceSet 개수
     */
    @Transactional(readOnly = true)
    public long getUserSentenceSetCount() {
        User user = userService.currentUser();
        return sentenceSetRepository.countByUser(user.getId());
    }

    /**
     * 현재 사용자의 문장 통계 (총 문장 개수, 학습 중인 문장, 학습 완료한 문장 개수)를 반환하는 메서드
     * @return 문장 통계
     */
    @Transactional(readOnly = true)
    public SentenceLearningStatusesDto getUserSentenceStatistics() {
        User user = userService.currentUser();

        long totalSentences = sentenceRepository.countByUserId(user.getId());
        long learningStatusInProgress = sentenceRepository.countByUserIdAndLearningStatusInProgress(user.getId());
        long learningStatusCompleted = sentenceRepository.countByUserIdAndLearningStatusCompleted(user.getId());

        return new SentenceLearningStatusesDto(totalSentences, learningStatusInProgress, learningStatusCompleted);
    }

    

}
