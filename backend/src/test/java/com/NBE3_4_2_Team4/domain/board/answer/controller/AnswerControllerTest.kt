package com.NBE3_4_2_Team4.domain.board.answer.controller

import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
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
class AnswerControllerTest {
    @Autowired
    private lateinit var answerService: AnswerService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @WithUserDetails("test@test.com")
    fun `답변 등록`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/questions/1/answers")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())


        val lastAnswer = answerService.itemsAll(1, 1).content[0]

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${lastAnswer.id}번 답변이 등록 되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(lastAnswer.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.created_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.modified_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.question_id").value(lastAnswer.questionId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.author_id").value(lastAnswer.authorId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.author_name").value(lastAnswer.authorName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(lastAnswer.content))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.selected").value(lastAnswer.selected))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.selected_at").value(lastAnswer.selectedAt))
    }

    @Test
    @WithUserDetails("admin@test.com")
    fun `답변 등록, with no input`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/questions/1/answers")
                    .content(
                        """
                                {
                                    "content": ""
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value(
                    """
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 2147483647
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `답변 등록, with no actor`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/questions/1/answers")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("401-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Unauthorized"))
    }

    @Test
    @WithUserDetails("admin@test.com")
    fun `답변 등록, with question author`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/questions/1/answers")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("400-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("작성자는 답변을 등록할 수 없습니다."))
    }

    @Test
    fun `특정 질문글 내 전체 답변 조회`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/questions/1/answers"))
            .andDo(MockMvcResultHandlers.print())

        val answersPage = answerService
            .items(1L, 1, 10)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.current_page_number").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.page_size").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages").value(answersPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_items").value(answersPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.has_more").value(answersPage.hasNext()))


        val answers = answersPage.content

        for (i in answers.indices) {
            val answer = answers[i]

            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].id").value(answer.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].created_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].modified_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].question_id").value(answer.questionId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].author_id").value(answer.authorId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].author_name").value(answer.authorName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].content").value(answer.content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].selected").value(answer.selected))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].selected_at").value(answer.selectedAt))
        }
    }

    @Test
    fun `존재하지 않는 11111111번 질문글 내 전체 답변 조회`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/questions/11111111/answers"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("404-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 질문글이 존재하지 않습니다."))
    }
}
