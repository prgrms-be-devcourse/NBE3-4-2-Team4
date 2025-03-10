package com.NBE3_4_2_Team4.domain.board.question.service

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.board.answer.repository.AnswerRepository
import com.NBE3_4_2_Team4.domain.board.question.dto.QuestionDto
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionCategoryRepository
import com.NBE3_4_2_Team4.domain.board.question.repository.QuestionRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType
import com.NBE3_4_2_Team4.standard.util.Ut
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val assetServiceFactory: AssetServiceFactory,
    private val answerRepository: AnswerRepository,
    private val memberRepository: MemberRepository
) {
    fun count(): Long = questionRepository.count()

    @Transactional
    fun write(title: String, content: String, categoryId: Long,
              author: Member, amount: Long, assetType: AssetType): QuestionDto {
        val category = questionCategoryRepository.findById(categoryId).orElseThrow()
        val assetService = assetServiceFactory.getService(assetType)
        val question = Question(
                title,
                content,
                author,
                category,
                assetType,
                amount
        )

        //질문글 작성 시 포인트 차감
        assetService.deduct(author.username, amount, AssetCategory.QUESTION)
        questionRepository.save(question)

        return QuestionDto(question)
    }

    fun findAll(): List<Question> = questionRepository.findAll()

    fun findLatest(): Optional<Question> = questionRepository.findFirstByOrderByIdDesc()

    @Transactional(readOnly = true)
    fun getQuestions(
        page: Int, pageSize: Int, searchKeyword: String, categoryId: Long,
        keywordType: QuestionSearchKeywordType, asset: String
    ): Page<QuestionDto> {
        val assetType = AssetType.fromValue(asset)

        return when {
            categoryId == 0L && asset == "ALL" -> findByListed(page, pageSize, searchKeyword, keywordType)
            categoryId != 0L && assetType == AssetType.ALL -> getQuestionsByCategory(categoryId, page, pageSize) // 카테고리 ID만 설정된 경우
            categoryId == 0L && assetType != AssetType.ALL -> getQuestionsByAssetType(assetType, page, pageSize) // assetType 만 설정된 경우
            else -> getQuestionsByCategoryAndAssetType(categoryId, page, pageSize, assetType) // 둘 다 설정된 경우
        }
    }

    private fun findByListed(
        page: Int, pageSize: Int, searchKeyword: String, searchKeywordType: QuestionSearchKeywordType
    ): Page<QuestionDto> {
        return questionRepository.findByKw(searchKeywordType, searchKeyword, Ut.pageable.makePageable(page, pageSize))
                .map { QuestionDto(it) }
    }

    @Transactional(readOnly = true)
    fun findByAnswerAuthor(memberId: Long, page: Int, pageSize: Int): Page<QuestionDto> {
        val author = memberRepository.findById(memberId).orElseThrow {
            ServiceException("404-1", "존재하지 않는 회원입니다.")
        }

        return questionRepository.findByAnswerAuthor(author, Ut.pageable.makePageable(page, pageSize))
                .map { QuestionDto(it) }
    }

    private fun getQuestionsByCategoryAndAssetType(
        categoryId: Long, page: Int, pageSize: Int, assetType: AssetType
    ): Page<QuestionDto> {
        val category = questionCategoryRepository.findById(categoryId).get()

        return questionRepository.findByCategoryAndAssetType(category, assetType, Ut.pageable.makePageable(page, pageSize))
                .map { QuestionDto(it) }
    }

    private fun getQuestionsByCategory(categoryId: Long, page: Int, pageSize: Int): Page<QuestionDto> {
        val category = questionCategoryRepository.findById(categoryId).get()

        return questionRepository.findByCategory(category, Ut.pageable.makePageable(page, pageSize))
                .map { QuestionDto(it) }
    }

    private fun getQuestionsByAssetType(assetType: AssetType, page: Int, pageSize: Int): Page<QuestionDto> {
       return questionRepository.findByAssetType(assetType, Ut.pageable.makePageable(page, pageSize))
                .map { QuestionDto(it) }
    }

    @Transactional(readOnly = true)
    fun findByUserListed(page: Int, pageSize: Int, username: String): Page<QuestionDto> {
        val actor = memberRepository.findByUsername(username)!!

        return questionRepository.findByAuthor(actor, Ut.pageable.makePageable(page, pageSize))
            .map { QuestionDto(it) }
    }

    @Transactional(readOnly = true)
    fun findByRecommends(page: Int, pageSize: Int): Page<QuestionDto> {
        return questionRepository.findRecommendedQuestions(Ut.pageable.makePageable(page, pageSize))
                .map { QuestionDto(it) }
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): QuestionDto {
        val question = questionRepository.findById(id).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }
        return QuestionDto(question)
    }

    fun findQuestionById(id: Long): Question {
        return questionRepository.findById(id).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }
    }

    @Transactional
    fun delete(id: Long, actor: Member) {
        val question = questionRepository.findById(id).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }
        question.checkActorCanDelete(actor)
        questionRepository.delete(question)
    }

    @Transactional
    fun update(
        id: Long, title: String, content: String, actor: Member, amount: Long, categoryId: Long, assetType: AssetType
    ): QuestionDto {
        val question = questionRepository.findById(id).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }
        question.checkActorCanModify(actor)
        val category = questionCategoryRepository.findById(categoryId).orElseThrow()
        val assetService = assetServiceFactory.getService(assetType)

        if (amount < question.amount) {
            throw ServiceException("400-1", "포인트/캐시는 기존보다 낮게 설정할 수 없습니다.")
        }
        // 기존 포인트/캐시 반환 후 새 포인트/캐시 차감
        assetService.accumulate(question.author.username, question.amount, AssetCategory.REFUND)

        question.modify(title, content, amount, category)
        assetService.deduct(actor.username, amount, AssetCategory.QUESTION_MODIFY)

        return QuestionDto(question)
    }

    @Transactional
    fun select(id: Long, answerId: Long): QuestionDto {
        val question = questionRepository.findById(id).orElseThrow {
            ServiceException("404-1", "게시글이 존재하지 않습니다.")
        }
        val answer = answerRepository.findById(answerId).get()
        val actor = AuthManager.getMemberFromContext()

        if (question.closed)
            throw ServiceException("400-1", "만료된 질문입니다.")

        if (actor == null)
            throw ServiceException("401-1", "로그인 후 이용해주세요.")

        if (!actor.equals(question.author))
            throw ServiceException("403-2", "작성자만 답변을 채택할 수 있습니다.")

        if (question.id != answer.question.id)
            throw ServiceException("403-3", "해당 질문글 내의 답변만 채택할 수 있습니다.")

        answer.select()
        question.selectedAnswer = answer
        question.closed = true

        //질문글 채택 시 채택된 답변 작성자 포인트 지급
        val assetService = assetServiceFactory.getService(question.assetType)
        assetService.accumulate(answer.author.username, question.amount, AssetCategory.ANSWER)

        return QuestionDto(question)
    }

    //7일 이상 질문들 closed 처리
    @Transactional
    fun closeQuestionsIfExpired(): Long {
        val expirationDate = LocalDateTime.now().minusDays(7)

        val expiredQuestions = questionRepository.findByCreatedAtBeforeAndClosed(expirationDate, false)

        for (question in expiredQuestions) {
            question.closed = true
            val assetService = assetServiceFactory.getService(question.assetType)

            if(question.answers.size == 0) {
                //답변자가 없는 경우 질문자에게 포인트 반환
                assetService.accumulate(question.author.username, question.amount, AssetCategory.REFUND)

                continue
            }

            val selectedPoint = if (question.amount != 0L && question.amount / question.answers.size > 1)
                    question.amount / question.answers.size
                    else 1L //최소 1포인트는 받을 수 있도록

            for (answer in question.answers) {
                //채택된건 아니므로 selected는 false 상태에서 포인트 지급된 날짜만 입력
                answer.selectedAt = LocalDateTime.now()

                //분배된 포인트 지급
                assetService.accumulate(answer.author.username, selectedPoint, AssetCategory.EXPIRED_QUESTION)
            }
        }

        return expiredQuestions.size.toLong()
    }

    @Transactional
    fun awardRankingPoints() { // 랭킹 순위 질문 포인트 지급
        val topQuestions = questionRepository.findRecommendedQuestions()

        val points = intArrayOf(1000, 500, 300) // 1등 1000, 2등 500, 3등 300포인트 지급
        var currentRank = 1 // 현재 순위
        var currentRecommendCount: Long = 0 // 현재 순위의 추천수
        var assignedRankCount = 0 // 현재 순위와 동일한 질문의 수
        var accumulatedCount = 0 // 직전까지 지급한 포인트를 받은 사람 수

        topQuestions.forEach { question ->
            val author = question.author
            val assetService = assetServiceFactory.getService(question.assetType)

            if (currentRecommendCount == question.getRecommendCount()) {
                assignedRankCount++
            } else {
                accumulatedCount += assignedRankCount
                currentRank += assignedRankCount
                assignedRankCount = 1
            }

            currentRecommendCount = question.getRecommendCount()

            // 순위가 3등 이내이고, 직전 순위가 3명을 넘지 않는 경우에만 해당 순위 포인트 지급
            if (accumulatedCount < 3 && currentRank <= 3) {
                val pointToAward = points[currentRank - 1] // 순위에 맞는 포인트(공동 순위 고려)
                author.let {
                    assetService.accumulate(it.username, pointToAward.toLong(), AssetCategory.RANKING)
                }
                // 포인트 지급 후 랭킹 포인트 지급 여부 true로 변경
                question.rankReceived = true
            }

            // 3등 이후는 포인트 지급하지 않음
            if (currentRank > 3) {
                return@forEach
            }
        }
    }
}
