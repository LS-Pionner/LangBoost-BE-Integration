package com.example.integration.repository;

import com.example.integration.entity.SentenceSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SentenceSetRepository extends JpaRepository<SentenceSet, Long> {
    // List<SentenceSet> 에서 sentenceSet.getSentenceList()를 조회할 경우 n+1 문제가 발생하므로 fetch join 사용
    @Query("select ss " +
            "from SentenceSet ss " +
            "left join fetch ss.sentenceList " +
            "where ss.isPublic = true " +
            "order by ss.createdDate desc")
    List<SentenceSet> findAllWhichPublic(Pageable pageable);

    @Query("select ss " +
            "from SentenceSet ss " +
            "left join fetch ss.sentenceList " +
            "where ss.title like %:keyword% " +
            "and ss.isPublic = true " +
            "order by ss.createdDate desc, ss.title asc")
    List<SentenceSet> findAllWithKeywordWhichPublic(String keyword, Pageable pageable);

    @Query("select ss " +
            "from SentenceSet ss " +
            "left join fetch ss.sentenceList " +
            "where ss.user.id = :userId " +
            "order by ss.createdDate desc")
    List<SentenceSet> findAllByUserId(Long userId, Pageable pageable);
}
