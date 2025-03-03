package com.example.integration.repository;

import com.example.integration.entity.Sentence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    Page<Sentence> findBySentenceSetId(Long sentenceSetId, Pageable pageable);

    // 특정 사용자가 소유한 모든 문장의 개수를 반환
    @Query("SELECT COUNT(s) FROM Sentence s WHERE s.sentenceSet.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    // 특정 사용자가 소유하고, 학습 상태가 '진행 중'인 문장의 개수를 반환
    @Query("SELECT COUNT(s) FROM Sentence s WHERE s.sentenceSet.user.id = :userId AND s.learningStatus = 'IN_PROGRESS'")
    long countByUserIdAndLearningStatusInProgress(@Param("userId") Long userId);

    // 특정 사용자가 소유하고, 학습 상태가 '완료'인 문장의 개수를 반환
    @Query("SELECT COUNT(s) FROM Sentence s WHERE s.sentenceSet.user.id = :userId AND s.learningStatus = 'COMPLETED'")
    long countByUserIdAndLearningStatusCompleted(@Param("userId") Long userId);
}
