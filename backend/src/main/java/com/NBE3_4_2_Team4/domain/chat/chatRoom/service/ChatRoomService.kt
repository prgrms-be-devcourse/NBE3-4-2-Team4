package com.NBE3_4_2_Team4.domain.chat.chatRoom.service

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer.answer
import com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion.question
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.chat.chat.dto.ChatRoomDto
import com.NBE3_4_2_Team4.domain.chat.chatRoom.entity.ChatRoom
import com.NBE3_4_2_Team4.domain.chat.chatRoom.repository.ChatRoomRepsitory
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepsitory,
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
        recipientNickname: String,
        name: String
    ): ChatRoomDto {
        val recipient = memberService.findByUsername(recipientNickname).orElseThrow {
            ServiceException(
                "404-2",
                "존재하지 않는 회원입니다."
            )
        }
        val actor = AuthManager.getNonNullMember()

        return ChatRoomDto(save(recipient, actor, name))
    }

    fun save(member1: Member, member2: Member, name: String): ChatRoom {
        val chatRoom = ChatRoom(name)

        chatRoom.member.add(member1)
        chatRoom.member.add(member2)

        return chatRoomRepository.save(chatRoom)
    }
}