package com.NBE3_4_2_Team4.domain.board.message.repository

import com.NBE3_4_2_Team4.domain.board.message.entity.Message
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<Message, Long> {
    fun findByReceiverIdAndDeletedByReceiverFalseOrderByCreatedAtDesc(receiverId: Long): List<Message>

    fun findBySenderIdAndDeletedBySenderFalseOrderByCreatedAtDesc(senderId: Long): List<Message>

    fun findAllByReceiverAndChecked(actor: Member, checked: Boolean): List<Message>
}
