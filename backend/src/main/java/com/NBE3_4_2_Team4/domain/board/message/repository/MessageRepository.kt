package com.NBE3_4_2_Team4.domain.board.message.repository

import com.NBE3_4_2_Team4.domain.board.message.entity.Message
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :receiverId AND m.deletedByReceiver = false ORDER BY m.createdAt DESC")
   fun findReceivedMessages(receiverId: Long): List<Message>

    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId AND m.deletedBySender = false ORDER BY m.createdAt DESC")
    fun findSentMessages(senderId: Long): List<Message>

    fun findAllByReceiverAndChecked(actor: Member, checked: Boolean): List<Message>
}
