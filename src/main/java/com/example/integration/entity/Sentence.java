package com.example.integration.entity;

import com.example.integration.config.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sentence")
@Entity
public class Sentence extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private String meaning;

    private String description;

    @Enumerated(EnumType.STRING)
    private LearningStatus learningStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_set_id")
    private SentenceSet sentenceSet;

    @Builder
    public Sentence(String content, String meaning, String description, LearningStatus learningStatus, SentenceSet sentenceSet) {
        this.content = content;
        this.meaning = meaning;
        this.description = description;
        this.learningStatus = learningStatus;
        this.sentenceSet = sentenceSet;

        sentenceSet.getSentenceList().add(this);
    }

    public void updateSentence(String content, String meaning, String description) {
        this.content = content;
        this.meaning = meaning;
        this.description = description;
    }

    public void updateLearningStatus(LearningStatus learningStatus) {
        this.learningStatus = learningStatus;
    }

}
