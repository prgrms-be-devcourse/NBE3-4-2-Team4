package com.NBE3_4_2_Team4.domain.sse.service

import com.NBE3_4_2_Team4.domain.sse.dto.ChatNotification
import com.NBE3_4_2_Team4.domain.sse.dto.RejectNotificationRequest
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationService(
    private val objectMapper: ObjectMapper
) {

    private val emitterMap = ConcurrentHashMap<Long, SseEmitter>()

    // 사용자가 SSE 구독
    fun subscribe(recipientId: Long): SseEmitter {
        val emitter = SseEmitter(60 * 1000)
        emitterMap[recipientId] = emitter

        emitter.onCompletion { emitterMap.remove(recipientId) }
        emitter.onTimeout { emitterMap.remove(recipientId) }

        return emitter
    }

    fun sendChatNotificationToRecipient(recipientId: Long) {
        val actor = AuthManager.getNonNullMember()

        val notification = ChatNotification(
            message = "새로운 채팅 요청이 있습니다.",
            senderName = actor.nickname,
            senderUsername = actor.username,
            senderId = actor.id!!,
            chatRoomId = 0
        )
        sendNotification(recipientId, notification)
    }

    // 특정 사용자에게 알림 전송
    fun sendNotification(recipientId: Long, notification: ChatNotification) {
        try {
            val notificationJson = objectMapper.writeValueAsString(notification)
            emitterMap[recipientId]?.send(SseEmitter.event().data(notificationJson))
        } catch (e: Exception) {
            // 연결이 끊어졌거나 타임아웃 발생 시 예외 처리
            emitterMap[recipientId]?.completeWithError(e)
            emitterMap.remove(recipientId)  // 오류가 발생한 클라이언트는 제거
        }
    }

    fun sendRejectNotification(recipientId: Long, message: String) {
        try {
            val notification = RejectNotificationRequest(
                message = message
            )

            val emitter = emitterMap[recipientId]
            emitter?.send(
                SseEmitter.event()
                    .data(notification)
                    .build()
            )
        } catch (e: Exception) {
            // 에러 발생 시 해당 emitter 제거
            emitterMap.remove(recipientId)
            throw e
        }
    }

    fun sendChatNotification(
        recipientId: Long,
        message: String,
        senderName: String,
        senderUsername: String,
        senderId: Long,
        chatRoomId: Long
    ) {
        val notification = mapOf(
            "message" to message,
            "sender_name" to senderName,
            "sender_username" to senderUsername,
            "sender_id" to senderId,
            "chat_room_id" to chatRoomId
        )

        val emitter = emitterMap[recipientId] ?: throw ServiceException("404-1", "알림 정보를 찾을 수 없습니다.")

        try {
            emitter.send(
                SseEmitter.event()
                    .data(objectMapper.writeValueAsString(notification))
            )
        } catch (e: Exception) {
            emitterMap.remove(recipientId)
            emitter.completeWithError(e)

            throw ServiceException("400-1", "알림 전송에 실패했습니다.")
        }
    }
}