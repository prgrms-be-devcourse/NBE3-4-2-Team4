package com.NBE3_4_2_Team4.domain.board.question.controller

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import com.NBE3_4_2_Team4.domain.board.question.dto.request.MyQuestionReqDto
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
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
class QuestionControllerTest {
    @Autowired
    private lateinit var questionService: QuestionService
    @Autowired
    private lateinit var answerService: AnswerService
    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var questionRepository: QuestionRepository
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var cashRequestJson: String
    private lateinit var pointRequestJson: String
    private lateinit var editRequestJson: String

    @BeforeEach
    fun setUp() {
        // 질문 작성 (CASH)
        val cashRequest = QuestionWriteReqDto("title22", "content22", 1L, 100, AssetType.CASH)
        cashRequestJson = objectMapper.writeValueAsString(cashRequest)

        // 질문 작성 (POINT)
        val pointRequest = QuestionWriteReqDto("title21", "content21", 2L, 100, AssetType.POINT)
        pointRequestJson = objectMapper.writeValueAsString(pointRequest)

        // 질문 수정
        val editRequest = QuestionWriteReqDto("title1 수정", "content1 수정", 1L, 100, AssetType.POINT)
        editRequestJson = objectMapper.writeValueAsString(editRequest)
    }

    @Test
    @DisplayName("전체 게시글 조회")
    fun t1() {
        val questions = questionService.findAll()
        assertThat(questions).hasSize(15)
    }

    @Test
    @DisplayName("다건 조회 with paging")
    fun t2_1() {
        val resultActions = mvc.perform(get("/api/questions?page=3&pageSize=7"))
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.current_page_number").value(3))
                .andExpect(jsonPath("$.page_size").value(7))
                .andExpect(jsonPath("$.total_items").value(15))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(1))
    }

    @Test
    @DisplayName("다건 조회 with paging, 포인트 질문만")
    fun t2_2() {
        val resultActions = mvc.perform(get("/api/questions?page=2&pageSize=7&assetType=POINT"))
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(2))
                .andExpect(jsonPath("$.page_size").value(7))
                .andExpect(jsonPath("$.total_items").value(12))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(5))
    }

    @Test
    @DisplayName("다건 조회 with paging, 캐시 질문만")
    fun t2_3() {
        val resultActions = mvc.perform(get("/api/questions?page=1&pageSize=10&assetType=CASH"))
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_items").value(3))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(3))
    }

    @Test
    @DisplayName("1번 게시글 조회")
    fun t3() {
        val resultActions = mvc.perform(get("/api/questions/1"))
                .andDo { print() }

        val question = questionRepository.findById(1L).orElseThrow()

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.created_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.modified_at").value(Matchers.startsWith(question.getModifiedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.title").value("성격 차이 극복 방법"))
                .andExpect(jsonPath("$.content").value("이 사람에게 어떻게 다가가야 할까요?"))
                .andExpect(jsonPath("$.name").value("관리자"))
    }

    @Test
    @DisplayName("질문 작성(포인트)")
    @WithUserDetails("admin@test.com")
    fun t4_1() {
        val resultActions = mvc.perform(
                post("/api/questions")
                        .content(pointRequestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        val question = questionService.findLatest().get()

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("16번 게시글 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.item.id").value(16L))
                .andExpect(jsonPath("$.data.item.title").value("title21"))
                .andExpect(jsonPath("$.data.item.content").value("content21"))
                .andExpect(jsonPath("$.data.item.category_name").value("건강"))
                .andExpect(jsonPath("$.data.item.asset_type").value("포인트"))
                .andExpect(jsonPath("$.data.item.created_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.modified_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.amount").value(100))
                .andExpect(jsonPath("$.data.total_count").value(16L))
    }

    @Test
    @DisplayName("질문 작성(캐시)")
    @WithUserDetails("admin@test.com")
    fun t4_2() {
        val resultActions = mvc.perform(
                post("/api/questions")
                        .content(cashRequestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        val question = questionService.findLatest().get()

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("17번 게시글 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.item.id").value(17L))
                .andExpect(jsonPath("$.data.item.title").value("title22"))
                .andExpect(jsonPath("$.data.item.content").value("content22"))
                .andExpect(jsonPath("$.data.item.category_name").value("연애"))
                .andExpect(jsonPath("$.data.item.asset_type").value("캐시"))
                .andExpect(jsonPath("$.data.item.created_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.modified_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.amount").value(100))
                .andExpect(jsonPath("$.data.total_count").value(16L))
    }

    @Test
    @DisplayName("1번 게시글 삭제")
    @WithUserDetails("admin@test.com")
    fun t5() {
        val resultActions = mvc.perform(
                delete("/api/questions/1")
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("게시글 삭제가 완료되었습니다."))
    }

    @Test
    @DisplayName("1번 게시글 수정")
    @WithUserDetails("admin@test.com")
    fun t6() {
        val resultActions = mvc.perform(
                put("/api/questions/1")
                        .content(editRequestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-2"))
                .andExpect(jsonPath("$.msg").value("1번 게시글 수정이 완료되었습니다."))
    }

    @Test
    @DisplayName("게시글 검색")
    fun t7() {
        val resultActions = mvc.perform(
                get("/api/questions?searchKeyword=노트북&keywordType=TITLE")
                )
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.total_items").value(1))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(1))
    }

    @Test
    @DisplayName("게시글 검색, with answer content")
    fun t7_1() {
        val resultActions = mvc.perform(
                        get("/api/questions?searchKeyword=답변&keywordType=ANSWER_CONTENT")
                )
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.total_items").value(2))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(2))
    }

    @Test
    @DisplayName("추천 게시글 조회")
    fun t8() {
        val resultActions = mvc.perform(get("/api/questions/recommends"))
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getRecommended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_items").value(2))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(2))
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    fun t9() {
        val resultActions = mvc.perform(get("/api/questions/100000"))
                .andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestion"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("게시글이 존재하지 않습니다."))
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제")
    @WithUserDetails("admin@test.com")
    fun t10() {
        val resultActions = mvc.perform(
                delete("/api/questions/100000")
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("게시글이 존재하지 않습니다."))
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    @WithUserDetails("admin@test.com")
    fun t11() {
        val resultActions = mvc.perform(
                put("/api/questions/100000")
                        .content(editRequestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("게시글이 존재하지 않습니다."))
    }

    @Test
    @DisplayName("글 작성자가 아닌 경우 게시글 수정 불가")
    @WithUserDetails("test@test.com")
    fun t12() {
        val resultActions = mvc.perform(
                put("/api/questions/1")
                        .content(editRequestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("게시글 작성자만 수정할 수 있습니다."))
    }

    @Test
    @DisplayName("글 작성자가 아닌 경우 게시글 삭제 불가")
    @WithUserDetails("test@test.com")
    fun t13() {
        val resultActions = mvc.perform(
                delete("/api/questions/1")
        ).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("게시글 작성자만 삭제할 수 있습니다."))
    }

    @Test
    @DisplayName("답변 채택")
    @WithUserDetails("admin@test.com")
    fun t14() {
        val answer = answerService.findById(1)
        val answerPoint = answer.author.point.amount

        val resultActions = mvc.perform(
                put("/api/questions/1/select/1")
        ).andDo { print() }

        val question = questionRepository.findById(1L).get()

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-3"))
                .andExpect(jsonPath("$.msg").value("1번 게시글의 1번 답변이 채택되었습니다."))
                .andExpect(jsonPath("$.data.id").value(question.getId()))
                .andExpect(jsonPath("$.data.title").value(question.title))
                .andExpect(jsonPath("$.data.content").value(question.content))
                .andExpect(jsonPath("$.data.category_name").value(question.category.name))
                .andExpect(jsonPath("$.data.created_at").exists())
                .andExpect(jsonPath("$.data.modified_at").exists())
                .andExpect(jsonPath("$.data.selected_answer.id").value(answer.getId()))
                .andExpect(jsonPath("$.data.selected_answer.created_at").exists())
                .andExpect(jsonPath("$.data.selected_answer.modified_at").exists())
                .andExpect(jsonPath("$.data.selected_answer.question_id").value(answer.question.id))
                .andExpect(jsonPath("$.data.selected_answer.content").value(answer.content))
                .andExpect(jsonPath("$.data.selected_answer.selected").value(true))
                .andExpect(jsonPath("$.data.selected_answer.selected_at").exists())
                .andExpect(jsonPath("$.data.closed").value(true))
                .andExpect(jsonPath("$.data.amount").value(question.amount))

        assertThat(answer.author.point.amount).isEqualTo(answerPoint + question.amount)
    }

    @Test
    @DisplayName("답변 채택, 이미 채택이 완료된 질문")
    @WithUserDetails("admin@test.com")
    fun t14_1() {
        val resultActions = mvc.perform(
                put("/api/questions/1/select/1")).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("select"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("만료된 질문입니다."))
    }

    @Test
    @DisplayName("답변 채택, with no actor")
    fun t14_2() {
        val resultActions = mvc.perform(
                put("/api/questions/1/select/1")).andDo { print() }

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result_code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("Unauthorized"))
    }

    @Test
    @DisplayName("답변 채택, with wrong actor")
    @WithUserDetails("test@test.com")
    fun t14_3() {
        val resultActions = mvc.perform(
                put("/api/questions/1/select/1")).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("select"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 답변을 채택할 수 있습니다."))
    }

    @Test
    @DisplayName("답변 채택, 다른 게시글의 답변 채택")
    @WithUserDetails("admin@test.com")
    fun t14_4() {
        val resultActions = mvc.perform(
                put("/api/questions/3/select/1")).andDo { print() }

        resultActions.andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("select"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-3"))
                .andExpect(jsonPath("$.msg").value("해당 질문글 내의 답변만 채택할 수 있습니다."))
    }

    @Test
    @DisplayName("카테고리로 질문 검색")
    fun t15() {
        val resultActions = mvc
                .perform(get("/api/questions?categoryId=1"))
                .andDo { print() }

        val questionPages = questionService.getQuestions( 1, 10, "",
                1L, QuestionSearchKeywordType.ALL, "ALL")

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(questionPages.totalPages))
                .andExpect(jsonPath("$.total_items").value(questionPages.totalElements))
                .andExpect(jsonPath("$.has_more").value(questionPages.hasNext()))

        val questions = questionPages.content

        for (i in questions.indices) {
            val question = questions[i]

            resultActions
                    .andExpect(jsonPath("$.items[$i].id").value(question.id))
                    .andExpect(jsonPath("$.items[$i].created_at").exists())
                    .andExpect(jsonPath("$.items[$i].modified_at").exists())
                    .andExpect(jsonPath("$.items[$i].title").value(question.title))
                    .andExpect(jsonPath("$.items[$i].content").value(question.content))
                    .andExpect(jsonPath("$.items[$i].name").value(question.name))
                    .andExpect(jsonPath("$.items[$i].recommend_count").value(question.recommendCount))
                    .andExpect(jsonPath("$.items[$i].closed").value(question.closed))
                    .andExpect(jsonPath("$.items[$i].amount").value(question.amount))
                    .andExpect(jsonPath("$.items[$i].author_id").value(question.authorId))
        }
    }

    @Test
    @DisplayName("현재 사용자가 작성한 질문 조회")
    @WithUserDetails("test@test.com")
    fun t16() {
        val username = AuthManager.getMemberFromContext().username
        val request = MyQuestionReqDto(username)
        val requestJson = objectMapper.writeValueAsString(request)

        val resultActions = mvc.perform(
                post("/api/questions/me")
                        .content(requestJson)
                        .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo { print() }

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getMyQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_items").value(7))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(7))

        val questions = questionService.findByUserListed(1, 10, username).content

        for (i in questions.indices) {
            val question = questions[i]

            resultActions
                    .andExpect(jsonPath("$.items[$i].id").value(question.id))
                    .andExpect(jsonPath("$.items[$i].created_at").exists())
                    .andExpect(jsonPath("$.items[$i].modified_at").exists())
                    .andExpect(jsonPath("$.items[$i].title").value(question.title))
                    .andExpect(jsonPath("$.items[$i].content").value(question.content))
                    .andExpect(jsonPath("$.items[$i].name").value(question.name))
                    .andExpect(jsonPath("$.items[$i].recommend_count").value(question.recommendCount))
                    .andExpect(jsonPath("$.items[$i].closed").value(question.closed))
                    .andExpect(jsonPath("$.items[$i].amount").value(question.amount))
                    .andExpect(jsonPath("$.items[$i].author_id").value(question.authorId))
        }
    }

    @Test
    @DisplayName("답변 작성자 기준으로 질문 검색")
    fun t17() {
        val resultActions = mvc
                .perform(get("/api/questions/answerer/2"))
                .andDo { print() }

        val questionPages = questionService.findByAnswerAuthor(2,  1, 10)

        resultActions
                .andExpect(handler().handlerType(QuestionController::class.java))
                .andExpect(handler().methodName("getAQuestionsByAnswerAuthor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(questionPages.totalPages))
                .andExpect(jsonPath("$.total_items").value(questionPages.totalElements))
                .andExpect(jsonPath("$.has_more").value(questionPages.hasNext()))

        val questions = questionPages.content

        for (i in questions.indices) {
            val question = questions[i]

            resultActions
                    .andExpect(jsonPath("$.items[$i].id").value(question.id))
                    .andExpect(jsonPath("$.items[$i].created_at").exists())
                    .andExpect(jsonPath("$.items[$i].modified_at").exists())
                    .andExpect(jsonPath("$.items[$i].title").value(question.title))
                    .andExpect(jsonPath("$.items[$i].content").value(question.content))
                    .andExpect(jsonPath("$.items[$i].name").value(question.name))
                    .andExpect(jsonPath("$.items[$i].recommend_count").value(question.recommendCount))
                    .andExpect(jsonPath("$.items[$i].closed").value(question.closed))
                    .andExpect(jsonPath("$.items[$i].amount").value(question.amount))
                    .andExpect(jsonPath("$.items[$i].author_id").value(question.authorId))
        }
    }
}
