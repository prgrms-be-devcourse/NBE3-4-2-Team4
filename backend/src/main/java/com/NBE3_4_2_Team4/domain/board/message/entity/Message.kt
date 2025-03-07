package com.NBE3_4_2_Team4.domain.board.message.entity

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Message(
    @ManyToOne
    var sender: Member,

    @ManyToOne
    var receiver: Member,

    @Column(length = 100)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String,

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    var isChecked: Boolean = false,

    var deletedBySender: Boolean = false,

    var deletedByReceiver: Boolean = false
) : BaseEntity() {

    fun checkSenderCanRead(actor: Member) {
        if (sender != actor) {
            throw ServiceException("403-1", "작성자만 쪽지를 읽을 수 있습니다.")
        }
    }

    fun checkReceiverCanRead(actor: Member) {
        if (receiver != actor) {
            throw ServiceException("403-2", "수신자만 쪽지를 읽을 수 있습니다.")
        }
    }
}
