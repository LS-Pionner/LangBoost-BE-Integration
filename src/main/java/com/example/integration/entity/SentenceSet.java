package com.example.integration.entity;

import com.example.integration.common.util.BaseEntity;
import com.example.integration.common.util.DateUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sentence_set")
@Entity
public class SentenceSet extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제목
    private String title;

    // 공용 조회 여부
    private boolean isPublic;

    // 마지막으로 조회한 날짜
    private LocalDate lastViewedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "sentenceSet")
    private List<Sentence> sentenceList = new ArrayList<>();

    @Builder
    public SentenceSet(String title, boolean isPublic, User user) {
        this.title = title;
        this.isPublic = isPublic;
        this.lastViewedDate = DateUtil.getLastViewedDate();
        this.user = user;

        user.getSentenceSetList().add(this);
    }

    public void updateSentenceSet(String title) {
        this.title = title;
    }

    public void updateLastViewedDate(LocalDate lastViewedDate) {
        this.lastViewedDate = lastViewedDate;
    }

}
