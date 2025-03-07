package com.NBE3_4_2_Team4.global.jpa.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseTime : BaseEntity() {
    @CreatedDate
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    lateinit var modifiedAt: LocalDateTime
}