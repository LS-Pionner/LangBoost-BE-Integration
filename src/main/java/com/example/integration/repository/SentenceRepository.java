package com.example.integration.repository;

import com.example.integration.entity.Sentence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    List<Sentence> findAllBySentenceSetId(Long sentenceSetId, Pageable pageable);
}
