package com.example.integration.repository;

import com.example.integration.entity.SentenceSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceSetRepository extends JpaRepository<SentenceSet, Long> {
    List<SentenceSet> findAllByUserId(Long userId);
}
