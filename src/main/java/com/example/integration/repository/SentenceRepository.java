package com.example.integration.repository;

import com.example.integration.entity.Sentence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    Page<Sentence> findByUserId(Long userId, Pageable pageable);
}
