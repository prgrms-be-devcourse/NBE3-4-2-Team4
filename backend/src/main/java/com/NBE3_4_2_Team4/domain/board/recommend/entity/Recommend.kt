package com.NBE3_4_2_Team4.domain.board.recommend.entity

import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Recommend(
    @ManyToOne(fetch = FetchType.LAZY)
    var question: Question,

    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member,

    @CreatedDate
    var recommendAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity()
