package com.example.integration.config.util;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@MappedSuperclass   // 상속받는 하위 클래스들이 아래의 필드를 갖게 함
@EntityListeners(AuditingEntityListener.class)  // 자동으로 값을 넣어줌
public class BaseEntity {

    @CreatedDate
    private LocalDate createdDate;

}
