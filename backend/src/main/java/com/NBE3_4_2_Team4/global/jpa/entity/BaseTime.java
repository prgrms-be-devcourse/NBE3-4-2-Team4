package com.NBE3_4_2_Team4.global.jpa.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseTime extends BaseEntity {

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime modifyDate;

}