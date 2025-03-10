package com.NBE3_4_2_Team4.domain.sse.controller

import com.NBE3_4_2_Team4.domain.sse.dto.ChatNotification
import com.NBE3_4_2_Team4.domain.sse.service.NotificationService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class NotificationControllerTest {
    @MockBean
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `SSE 구독 테스트`() {
        val recipientId = 1L

        // MockMvc로 GET 요청을 보내고, SSE 연결 확인
        mvc.perform(
            MockMvcRequestBuilders.get("/api/notifications/$recipientId")
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "text/event-stream"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `알림 전송 테스트`() {
        val recipientId = 1L
        val senderName = "Sender"
        val senderUserName = "SenderUsername"
        val notification = ChatNotification(
            message = "새로운 채팅 요청이 있습니다.",
            senderName = senderName,
            senderUsername = senderUserName
        )

        // 알림을 보내는 서비스 호출을 검증
        Mockito.doNothing().`when`(notificationService).sendChatNotificationToRecipient(recipientId)

        // MockMvc로 POST 요청을 보내고, 알림 전송 확인
        mvc.perform(
            MockMvcRequestBuilders.post("/api/notifications/$recipientId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(notification)) // JSON으로 직렬화된 데이터
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // 200 OK 상태를 확인
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("채팅 참여 요청이 전송되었습니다."))
            .andDo(MockMvcResultHandlers.print())
    }
}