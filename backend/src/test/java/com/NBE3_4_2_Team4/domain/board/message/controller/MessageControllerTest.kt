package com.NBE3_4_2_Team4.domain.board.message.controller

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto
import com.NBE3_4_2_Team4.domain.board.message.dto.request.MessageWriteReqDto
import com.NBE3_4_2_Team4.domain.board.message.repository.MessageRepository
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class MessageControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Test
    @DisplayName("내가 보낸 쪽지 목록 조회")
    @WithUserDetails("test@test.com")
    fun t1_1() {
        val author = AuthManager.getNonNullMember()

        val resultActions = mvc.perform(
                get("/api/messages/send")
        ).andDo { print() }

        val messages = messageRepository.findSentMessages(author.id).map(::MessageDto)

        resultActions.andExpect(handler().handlerType(MessageController::class.java))
                .andExpect(handler().methodName("getSentMessages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].sender_name").value(everyItem(`is`("테스트 유저"))))

        for (i in messages.indices) {
            val message = messages[i]

            resultActions
                    .andExpect(jsonPath("$[$i].created_at").exists())
                    .andExpect(jsonPath("$[$i].title").value(message.title))
                    .andExpect(jsonPath("$[$i].content").value(message.content))
                    .andExpect(jsonPath("$[$i].sender_name").value(message.senderName))
                    .andExpect(jsonPath("$[$i].receiver_name").value(message.receiverName))
                    .andExpect(jsonPath("$[$i].checked").value(message.checked))
        }
    }

    @Test
    @DisplayName("내가 받은 쪽지 목록 조회")
    @WithUserDetails("test@test.com")
    fun t1_2() {
        val author = AuthManager.getNonNullMember()

        val resultActions = mvc.perform(
                get("/api/messages/receive")
        ).andDo { print() }

        val messages = messageRepository.findReceivedMessages(author.getId()).map(::MessageDto)

        resultActions.andExpect(handler().handlerType(MessageController::class.java))
                .andExpect(handler().methodName("getReceivedMessages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].receiver_name").value(everyItem(`is`("테스트 유저"))))

        for (i in messages.indices) {
            val message = messages[i]
            resultActions
                    .andExpect(jsonPath("$[$i].title").value(message.title))
                    .andExpect(jsonPath("$[$i].content").value(message.content))
                    .andExpect(jsonPath("$[$i].sender_name").value(message.senderName))
                    .andExpect(jsonPath("$[$i].receiver_name").value(message.receiverName))
                    .andExpect(jsonPath("$[$i].created_at").exists())
                    .andExpect(jsonPath("$[$i].checked").value(message.checked))
        }
    }

    @Test
    @DisplayName("쪽지 단건조회")
    @WithUserDetails("test@test.com")
    fun t2_1() {
        val resultActions = mvc.perform(
                get("/api/messages/1")
        ).andDo { print() }

        val message = MessageDto(messageRepository.findById(1L).get())

        resultActions.andExpect(handler().handlerType(MessageController::class.java))
                .andExpect(handler().methodName("getMessage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(message.title))
                .andExpect(jsonPath("$.content").value(message.content))
                .andExpect(jsonPath("$.sender_name").value(message.senderName))
                .andExpect(jsonPath("$.receiver_name").value(message.receiverName))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.checked").value(message.checked))
    }

    @Test
    @DisplayName("쪽지 작성")
    @WithUserDetails("admin@test.com")
    fun t3() {
        val request = MessageWriteReqDto("쪽지 제목", "쪽지 내용", "테스트 유저")
        val requestJson = objectMapper.writeValueAsString(request)

        val resultActions = mvc.perform(
                post("/api/messages")
                        .content(requestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(MessageController::class.java))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("쪽지를 성공적으로 보냈습니다."))
                .andExpect(jsonPath("$.data.title").value("쪽지 제목"))
                .andExpect(jsonPath("$.data.content").value("쪽지 내용"))
                .andExpect(jsonPath("$.data.sender_name").value("관리자"))
                .andExpect(jsonPath("$.data.receiver_name").value("테스트 유저"))
                .andExpect(jsonPath("$.data.created_at").exists())
                .andExpect(jsonPath("$.data.checked").value(false))
    }

    @Test
    @DisplayName("내가 받은/보낸 쪽지 삭제")
    @WithUserDetails("test@test.com")
    fun t4_1() {
        val ids = listOf(1L, 3L)

        val resultActions = mvc.perform(
                delete("/api/messages")
                        .content(ObjectMapper().writeValueAsString(ids))
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(MessageController::class.java))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-2"))
                .andExpect(jsonPath("$.msg").value("${ids.size}개의 쪽지를 삭제하였습니다."))
    }

    @Test
    @DisplayName("쪽지 확인(읽기)")
    @WithUserDetails("admin@test.com")
    fun t5_1() {
        val ids = listOf(1L, 3L)

        val resultActions = mvc.perform(
                put("/api/messages")
                        .content(ObjectMapper().writeValueAsString(ids))
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(MessageController::class.java))
                .andExpect(handler().methodName("check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-3"))
                .andExpect(jsonPath("$.msg").value("${ids.size}개의 쪽지를 읽었습니다."))
    }
}
