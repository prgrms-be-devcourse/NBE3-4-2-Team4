package com.NBE3_4_2_Team4.domain.chat.chatRoom.service

import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatRoomDto
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoomMember
import com.NBE3_4_2_Team4.domain.chat.chatRoom.repository.ChatRoomMemberRepository
import com.NBE3_4_2_Team4.domain.chat.chatRoom.repository.ChatRoomRepsitory
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import com.NBE3_4_2_Team4.domain.sse.service.NotificationService
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.util.Ut
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepsitory,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val notificationService: NotificationService,
    private val memberService: MemberService
) {
    fun count(): Long {
        return chatRoomRepository.count()
    }

    fun findById(id: Long): ChatRoom {
        return chatRoomRepository.findById(id).orElseThrow {
            ServiceException(
                "404-1",
                "해당 채팅방이 존재하지 않습니다."
            )
        }
    }

    @Transactional
    fun create(
        recipientUsername: String,
        name: String
    ): ChatRoomDto {
        val recipient = memberService.findByUsername(recipientUsername).orElseThrow {
            ServiceException(
                "404-2",
                "존재하지 않는 회원입니다."
            )
        }

        val actor = AuthManager.getNonNullMember()
        val chatRoom = save(recipient, actor, name)

        val memberList = chatRoomMemberRepository.findByChatRoom(chatRoom)
            .map { it.member.nickname }

        return ChatRoomDto(chatRoom, memberList)
    }

    @Transactional
    fun save(member1: Member, member2: Member, name: String): ChatRoom {
        val chatRoom = ChatRoom(name)

        chatRoomMemberRepository.save(ChatRoomMember(chatRoom, member1))
        chatRoomMemberRepository.save(ChatRoomMember(chatRoom, member2))

        return chatRoomRepository.save(chatRoom)
    }

    @Transactional(readOnly = true)
    fun itemsAll(page: Int, pageSize: Int): Page<ChatRoomDto>{
        val chatRooms = chatRoomRepository.findAll(Ut.pageable.makePageable(page, pageSize))

        return chatRooms.map {
            val memberList = chatRoomMemberRepository.findByChatRoom(it)
                .map { chatRoomMember -> chatRoomMember.member.nickname }

            ChatRoomDto(
                it,
                memberList
            )
        }
    }

    @Transactional(readOnly = true)
    fun myChatRooms(page: Int, pageSize: Int, me: Member =  AuthManager.getNonNullMember()): Page<ChatRoomDto> {
        val chatRoomMember = chatRoomMemberRepository.findByMember(me, Ut.pageable.makePageable(page, pageSize))

        return chatRoomMember.map { member ->
            val chatRoom = member.chatRoom
            val memberList = chatRoomMemberRepository.findByChatRoom(chatRoom)
                .map { it.member.nickname }

            ChatRoomDto(
                chatRoom,
                memberList
            )
        }
    }

    @Transactional
    fun exitChatRoom(id: Long) {
        val actor =  AuthManager.getNonNullMember()
        val chatRoom =  chatRoomMemberRepository.findByChatRoom(findById(id))
            .first { it.member == actor }

        chatRoomMemberRepository.delete(chatRoom)
        findById(id).isClosed = true
    }

    @Transactional(readOnly = true)
    fun getChatRoom(chatRoomId: Long): ChatRoomDto {
        val chatRoom = findById(chatRoomId)
        val memberList = chatRoomMemberRepository.findByChatRoom(chatRoom)
            .map { it.member.nickname }

        return ChatRoomDto(chatRoom, memberList)
    }
}