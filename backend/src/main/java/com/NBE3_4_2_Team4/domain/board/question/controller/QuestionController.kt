package com.NBE3_4_2_Team4.domain.board.question.controller

import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto
import com.NBE3_4_2_Team4.domain.board.question.dto.request.MyQuestionReqDto
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto
import com.NBE3_4_2_Team4.domain.board.question.dto.response.QuestionWriteResDto
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.dto.PageDto
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "지식인 질문 관리", description = "지식인 질문 관련 API")
@RequestMapping("/api/questions")
class QuestionController(
    private val questionService: QuestionService
) {
    @GetMapping
    @Operation(summary = "질문 글 조회 with 검색", description = "지식인 질문을 검색어, 페이지, 페이지 크기를 기준으로 조회")
    fun getQuestions(
        @RequestParam(defaultValue = "") searchKeyword: String,
        @RequestParam(defaultValue = "ALL") keywordType: QuestionSearchKeywordType,
        @RequestParam(defaultValue = "ALL") assetType: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(defaultValue = "0") categoryId: Long
    ): PageDto<QuestionDto> {
        return PageDto(
            questionService.getQuestions(page, pageSize, searchKeyword, categoryId, keywordType, assetType)
        )
    }

    @GetMapping("/recommends")
    @Operation(summary = "추천 글 조회", description = "추천 수 기준으로 내림차순 정렬")
    fun getRecommended(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): PageDto<QuestionDto> {
        return PageDto(
                questionService.findByRecommends(page, pageSize)
        )
    }

    @GetMapping("/answerer/{memberId}")
    @Operation(summary = "답변 작성자 기준 질문글 조회", description = "내가 남긴 답변 보기, 혹은 누군가가 답변을 남긴 질문글들 보기")
    fun getAQuestionsByAnswerAuthor(
            @PathVariable memberId: Long,
            @RequestParam(defaultValue = "1") page: Int,
            @RequestParam(defaultValue = "10") pageSize: Int
    ): PageDto<QuestionDto> {
        return PageDto(
                questionService.findByAnswerAuthor(memberId, page, pageSize)
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "질문 글 단건조회", description = "질문 id에 해당하는 글 조회")
    fun getQuestion(@PathVariable id: Long): QuestionDto {
        return questionService.findById(id)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "질문 삭제", description = "질문 id에 해당하는 글 삭제, 작성자만 삭제 가능")
    fun delete(@PathVariable id: Long): RsData<Void> {
        val actor = AuthManager.getNonNullMember()
        questionService.delete(id, actor)

        return RsData(
                "200-1",
                "게시글 삭제가 완료되었습니다."
        )
    }

    @PostMapping
    @Operation(summary = "질문 등록")
    fun write(@RequestBody @Valid reqBody: QuestionWriteReqDto): RsData<QuestionWriteResDto> {
        val author = AuthManager.getNonNullMember()
        val question = questionService.write(reqBody.title, reqBody.content,
                reqBody.categoryId, author, reqBody.amount, reqBody.assetType)

        return RsData(
                "201-1",
                "${question.id}번 게시글 생성이 완료되었습니다.",
                QuestionWriteResDto(
                        question,
                        questionService.count()
                )
        )
    }

    @PutMapping("/{id}")
    @Operation(summary = "질문 수정", description = "질문 id에 해당하는 글 수정, 작성자만 수정 가능")
    fun update(@PathVariable id: Long, @RequestBody @Valid reqBody: QuestionWriteReqDto): RsData<QuestionDto> {
        val actor = AuthManager.getNonNullMember()
        val question = questionService.update(id, reqBody.title, reqBody.content,
                actor, reqBody.amount, reqBody.categoryId, reqBody.assetType)

        return RsData(
                "200-2",
                "${id}번 게시글 수정이 완료되었습니다.",
                question
        )
    }

    @PutMapping("/{id}/select/{answerId}")
    @Operation(summary = "답변 채택", description = "질문 id에 해당하는 질문 내 해당 답변 id를 채택, 작성자만 채택 가능, 답변 채택 시 답변 작성자에게 포인트를 지급")
    fun select(
            @PathVariable id: Long,
            @PathVariable answerId: Long
    ): RsData<QuestionDto> {
        val question = questionService.select(id, answerId)

        return RsData(
                "200-3",
                "${id}번 게시글의 ${answerId}번 답변이 채택되었습니다.",
                question
        )
    }

    @PostMapping("/me")
    @Operation(summary = "내 질문 조회", description = "현재 사용자의 질문 조회")
    fun getMyQuestions(
            @RequestParam(defaultValue = "1") page: Int,
            @RequestParam(defaultValue = "10") pageSize: Int,
            @RequestBody @Valid reqBody: MyQuestionReqDto
            ): PageDto<QuestionDto> {
        return PageDto(questionService.findByUserListed(page, pageSize, reqBody.username))
    }
}
