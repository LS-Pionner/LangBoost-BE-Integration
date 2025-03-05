package com.example.integration.repository;

import com.example.integration.entity.SentenceSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SentenceSetRepository extends JpaRepository<SentenceSet, Long> {
    // List<SentenceSet> 에서 sentenceSet.getSentenceList()를 조회할 경우 n+1 문제가 발생하므로 fetch join 사용
    @Query("SELECT ss " +
            "FROM SentenceSet ss " +
            "LEFT JOIN FETCH ss.sentenceList " +
            "WHERE ss.isPublic = true " +
            "ORDER BY ss.createdDate desc")
    List<SentenceSet> findAllWhichPublic(Pageable pageable);

    @Query("SELECT ss " +
            "FROM SentenceSet ss " +
            "LEFT JOIN FETCH ss.sentenceList " +
            "WHERE ss.title like %:keyword% " +
            "AND ss.isPublic = true " +
            "ORDER BY ss.createdDate desc, ss.title asc")
    List<SentenceSet> findAllWithKeywordWhichPublic(String keyword, Pageable pageable);

    @Query("SELECT ss " +
            "FROM SentenceSet ss " +
            "LEFT JOIN FETCH ss.sentenceList " +
            "WHERE ss.user.id = :userId " +
            "ORDER BY ss.createdDate desc")
    List<SentenceSet> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(ss) > 0 " +
            "FROM SentenceSet ss " +
            "WHERE ss.id = :sentenceSetId " +
            "AND ss.user.id = :userId")
    boolean existsByUserIdAndSentenceSetId(Long userId, Long sentenceSetId);
}
