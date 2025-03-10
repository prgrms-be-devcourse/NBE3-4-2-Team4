package com.NBE3_4_2_Team4.domain.chat.chatRoom.controller

import com.NBE3_4_2_Team4.domain.chat.chat.service.ChatService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ChatControllerTest {
    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @WithUserDetails("test@test.com")
    fun `채팅 쓰기`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/chatRooms/1/chats")
                    .content(
                        """
                        {
                            "content": "새로운 채팅",
                            "sender_username": "admin@test.com",
                            "chat_room_id": "1"
                        }
                        """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())


        val lastChat = chatService.items(1).last()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ChatController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${lastChat.id}번 메세지가 전송되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(lastChat.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.created_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.chat_room_id").value(lastChat.chatRoomId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.sender_id").value(lastChat.senderId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.sender_name").value(lastChat.senderName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(lastChat.content))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.is_read").value(lastChat.isRead))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.read_at").isEmpty)
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `채팅방 기준 채팅 다건 조회`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/chatRooms/1/chats"))
            .andDo(MockMvcResultHandlers.print())

        val chats = chatService.items(1)

        for (i in chats.indices) {
            val chat = chats[i]

            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].id").value(chat.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].created_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].chat_room_id").value(chat.chatRoomId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].sender_id").value(chat.senderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].sender_name").value(chat.senderName))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].content").value(chat.content))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].is_read").value(chat.isRead))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].read_at").value(chat.readAt))
        }
    }
}