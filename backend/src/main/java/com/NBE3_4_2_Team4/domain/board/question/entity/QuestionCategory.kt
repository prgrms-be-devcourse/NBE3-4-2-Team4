package com.NBE3_4_2_Team4.domain.board.question.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class QuestionCategory(
        @Column(length = 100)
        var name: String = ""
) : BaseEntity() {

    fun checkActorCanCreate(actor: Member) {
        if (actor.role != Member.Role.ADMIN) {
            throw ServiceException("403-1", "관리자만 카테고리를 관리할 수 있습니다.")
        }
    }

    fun checkActorCanDelete(actor: Member) {
        if (actor.role != Member.Role.ADMIN) {
            throw ServiceException("403-2", "관리자만 카테고리를 관리할 수 있습니다.")
        }
    }
}

