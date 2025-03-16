package com.example.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LearningStatus {
    IN_PROGRESS("학습 중"),
    COMPLETED("학습 완료")
    ;

    private final String description;
}
