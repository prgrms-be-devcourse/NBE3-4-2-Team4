package com.NBE3_4_2_Team4.domain.board.message.repository

import com.NBE3_4_2_Team4.domain.board.message.entity.Message
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MessageRepository : JpaRepository<Message, Long> {
    fun findByReceiverIdAndDeletedByReceiverFalseOrderByCreatedAtDesc(pageable: Pageable, receiverId: Long): Page<Message>

    fun findBySenderIdAndDeletedBySenderFalseOrderByCreatedAtDesc(pageable: Pageable, senderId: Long): Page<Message>

    fun findAllByReceiverAndChecked(actor: Member, checked: Boolean): List<Message>
}
