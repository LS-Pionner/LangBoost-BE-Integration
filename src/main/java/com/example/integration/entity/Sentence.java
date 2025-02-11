package com.example.integration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sentence")
@Entity
public class Sentence {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sentence;

    private String description;

    @Column(nullable = false)
    private LocalDate lastViewedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToMany(mappedBy = "sentence", cascade = CascadeType.ALL)
//    private List<Word> words;

    @Builder
    public Sentence(String sentence, String description, LocalDate lastViewedDate, User user) {
        this.sentence = sentence;
        this.description = description;
        this.lastViewedDate = lastViewedDate;
        this.user = user;

        user.getSentenceList().add(this);
    }

    public void updateSentence(String newSentence, String newDescription) {
        this.sentence = newSentence;
        this.description = newDescription;
    }

    public void updateLastViewedDate(LocalDate newLastViewedDate) {
        this.lastViewedDate = newLastViewedDate;
    }

}
