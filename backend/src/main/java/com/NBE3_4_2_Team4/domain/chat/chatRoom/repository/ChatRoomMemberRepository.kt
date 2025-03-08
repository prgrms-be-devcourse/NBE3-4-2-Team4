package com.NBE3_4_2_Team4.domain.chat.chatRoom.repository

import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoomMember
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long> {
    fun findByChatRoom(chatRoom: ChatRoom): List<ChatRoomMember>
    fun findByMember(member: Member, pageable: Pageable): Page<ChatRoomMember>
}