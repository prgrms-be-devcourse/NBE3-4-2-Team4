package com.NBE3_4_2_Team4.domain.chat.chatRoom.controller

import com.NBE3_4_2_Team4.domain.chat.chatRoom.repository.ChatRoomMemberRepository
import com.NBE3_4_2_Team4.domain.chat.chatRoom.service.ChatRoomService
import com.NBE3_4_2_Team4.global.security.AuthManager
import org.assertj.core.api.Assertions
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
class ChatRoomControllerTest {
    @Autowired
    private lateinit var chatRoomService: ChatRoomService

    @Autowired
    private lateinit var chatRoomMemberRepository: ChatRoomMemberRepository

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @WithUserDetails("test@test.com")
    fun `채팅방 생성`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/chatRooms")
                    .content(
                        """
                        {
                            "name": "채팅방 new",
                            "recipient_username": "test2@test.com"
                        }
                        """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())


        val lastChatRoom = chatRoomService.itemsAll(1, 1).content[0]

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ChatController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("create"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${lastChatRoom.id}번 채팅방이 생성되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(lastChatRoom.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.created_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.modified_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(lastChatRoom.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.chat_count").value(lastChatRoom.chatCount))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.new_chat_count").value(lastChatRoom.newChatCount))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.is_closed").value(lastChatRoom.isClosed))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.member_list[0]").value(lastChatRoom.memberList[0]))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.member_list[1]").value(lastChatRoom.memberList[1]))
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `로그인 한 회원의 채팅방 전체 조회`() {
        val member = AuthManager.getNonNullMember()

        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/chatRooms"))
            .andDo(MockMvcResultHandlers.print())

        val chatRoomPage = chatRoomService
            .myChatRooms(1, 10, member)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ChatController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("myChatRooms"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.current_page_number").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.page_size").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages").value(chatRoomPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_items").value(chatRoomPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.has_more").value(chatRoomPage.hasNext()))

        val chatRooms = chatRoomPage.content

        for (i in chatRooms.indices) {
            val chatRoom = chatRooms[i]

            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].id").value(chatRoom.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].created_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].modified_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].name").value(chatRoom.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].chat_count").value(chatRoom.chatCount))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].new_chat_count").value(chatRoom.newChatCount))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].is_closed").value(chatRoom.isClosed))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].member_list[0]").value(chatRoom.memberList[0]))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].member_list[1]").value(chatRoom.memberList[1]))
        }
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `채팅방 나가기`() {
        val chatRoom = chatRoomService.findById(1)

        val resultActions = mvc
            .perform(MockMvcRequestBuilders.delete("/api/chatRooms/1"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ChatController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("exitChatRoom"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("200-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${chatRoom.id}번 채팅방에서 나갔습니다."))

        Assertions.assertThat(
            chatRoomMemberRepository.findByChatRoom(chatRoom)
                .filter { it.member.username == "test@test.com" }
                .size
        ).isEqualTo(0)

        Assertions.assertThat(chatRoom.isClosed).isTrue()
    }
}