package com.NBE3_4_2_Team4.domain.board.message.service

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto
import com.NBE3_4_2_Team4.domain.board.message.entity.Message
import com.NBE3_4_2_Team4.domain.board.message.entity.MessageType
import com.NBE3_4_2_Team4.domain.board.message.repository.MessageRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository
) {
    fun count(): Long = messageRepository.count()

    @Transactional(readOnly = true)
    fun getSentMessages(): List<MessageDto> = getMessagesByType(MessageType.SENT)

    @Transactional(readOnly = true)
    fun getReceivedMessages(): List<MessageDto> = getMessagesByType(MessageType.RECEIVED)

    private fun getMessagesByType(type: MessageType): List<MessageDto> {
        val actor = AuthManager.getNonNullMember()
        val messages = when (type) {
            MessageType.RECEIVED -> messageRepository.findByReceiverIdAndDeletedByReceiverFalseOrderByCreatedAtDesc(actor.id)
            MessageType.SENT -> messageRepository.findBySenderIdAndDeletedBySenderFalseOrderByCreatedAtDesc(actor.id)
        }

        return messages.map(::MessageDto)
    }

    @Transactional(readOnly = true)
    fun getUnreadMessages(): Long {
        val actor = AuthManager.getNonNullMember()
        val messages = messageRepository.findAllByReceiverAndChecked(actor, false)

        return messages.map(::MessageDto).size.toLong()
    }

    @Transactional(readOnly = true)
    fun getMessage(id: Long): MessageDto {
        val actor = AuthManager.getNonNullMember()
        val message = messageRepository.findById(id)
                .orElseThrow { ServiceException("404-1", "존재하지 않는 메시지입니다.") }

        message.checkSenderCanRead(actor)
        return MessageDto(message)
    }

    @Transactional
    fun write(receiverName: String, title: String, content: String): MessageDto {
        val sender = AuthManager.getNonNullMember()
        val receiver = memberRepository.findByNickname(receiverName).get()

        val message = Message(
                sender = sender,
                receiver = receiver,
                title = title,
                content = content,
                checked = false
        )
        messageRepository.save(message)
        return MessageDto(message)
    }

    @Transactional
    fun delete(ids: List<Long>) {
        val actor = AuthManager.getNonNullMember()
        val messages = messageRepository.findAllById(ids)

        messages.forEach { message ->
            if (message == null) {
                throw ServiceException("404-1", "존재하지 않는 메시지가 포함되어 있습니다.")
            }

            // 발신자가 삭제
            if (message.sender == actor) {
                message.deletedBySender = true
            }
            // 수신자가 삭제
            if (message.receiver == actor) {
                message.deletedByReceiver = true
            }
        }

        // 발신자와 수신자 모두 삭제한 경우 실제 DB 에서 삭제
        val toDelete = messages.filter { it.deletedBySender && it.deletedByReceiver }
        messageRepository.deleteAll(toDelete)
    }

    @Transactional
    fun readMessage(ids: List<Long>) {
        val actor = AuthManager.getNonNullMember()
        val messages = messageRepository.findAllById(ids)

        messages.forEach { message ->
            if (message == null) {
                throw ServiceException("404-1", "존재하지 않는 메시지가 포함되어 있습니다.")
            }
            message.checkReceiverCanRead(actor)
            message.checked = true
        }

    }
}
