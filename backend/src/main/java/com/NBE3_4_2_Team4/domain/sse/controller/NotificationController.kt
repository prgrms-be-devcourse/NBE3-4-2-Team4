package com.NBE3_4_2_Team4.domain.sse.controller

import com.NBE3_4_2_Team4.domain.sse.service.NotificationService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    // 사용자별 SSE 연결을 관리하는 Map
    private val emitterMap = ConcurrentHashMap<Long, SseEmitter>()

    @GetMapping("/{recipientId}")
    fun subscribe(@PathVariable recipientId: Long): ResponseEntity<SseEmitter> {
        val emitter = notificationService.subscribe(recipientId)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
            .body(emitter)
    }

    @PostMapping("/{recipientId}")
    fun sendNotification(
        @PathVariable recipientId: Long
    ): RsData<Empty> {
        // 알림 전송 로직
        notificationService.sendChatNotificationToRecipient(recipientId)

        return RsData(
            "200-1",
            "채팅 참여 요청이 전송되었습니다."
        )
    }
}