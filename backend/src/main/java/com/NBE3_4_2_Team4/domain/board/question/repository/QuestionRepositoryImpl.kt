package com.NBE3_4_2_Team4.domain.board.question.repository

import com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer.answer
import com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion.question
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.querydsl.core.types.Expression
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class QuestionRepositoryImpl(
        private val jpaQueryFactory: JPAQueryFactory
) : QuestionRepositoryCustom {

    override fun findByKw(kwType: QuestionSearchKeywordType, kw: String, pageable: Pageable): Page<Question> {
        val builder = BooleanBuilder()

        if(kw.isNotBlank())
            applyKeywordFilter(kwType, kw, builder)

        return getQuestions(builder, pageable)
    }

    override fun findByAnswerAuthor(author: Member, pageable: Pageable): Page<Question> {
        val builder = BooleanBuilder()

        answerAuthorFilter(author, builder)

        return getQuestions(builder, pageable)
    }

    private fun applyKeywordFilter(kwType: QuestionSearchKeywordType, kw: String, builder: BooleanBuilder) {
        when (kwType) {
            QuestionSearchKeywordType.TITLE -> builder.and(question.title.containsIgnoreCase(kw))
            QuestionSearchKeywordType.CONTENT -> builder.and(question.content.containsIgnoreCase(kw))
            QuestionSearchKeywordType.AUTHOR -> builder.and(question.author.nickname.containsIgnoreCase(kw))
            QuestionSearchKeywordType.ANSWER_CONTENT -> builder.and(hasAnswerContainingKeyword(kw))
            else -> builder.and(
                    question.title.containsIgnoreCase(kw)
                            .or(question.content.containsIgnoreCase(kw))
                            .or(question.author.nickname.containsIgnoreCase(kw))
                            .or(hasAnswerContainingKeyword(kw))
            )
        }
    }

    //answer content 내에 kw가 포함된 행이 하나라도 존재할 경우 true
    private fun hasAnswerContainingKeyword(kw: String): BooleanExpression {
        return JPAExpressions
                .selectOne()
                .from(answer)
                .where(answer.question.eq(question)
                    .and(answer.content.containsIgnoreCase(kw)))
                .exists()
    }

    private fun createPostsQuery(builder: BooleanBuilder): JPAQuery<Question> {
        return jpaQueryFactory
                .select(question)
                .from(question)
                .where(builder)
    }

    private fun applySorting(pageable: Pageable, questionsQuery: JPAQuery<Question>) {
        for (o in pageable.sort) {
            val pathBuilder: PathBuilder<*> = PathBuilder<Any?>(question.type, question.metadata)
            questionsQuery.orderBy(
                OrderSpecifier(
                    if (o.isAscending) Order.ASC else Order.DESC,
                    pathBuilder[o.property] as Expression<Comparable<*>>
                )
            )
        }
    }

    private fun createTotalQuery(builder: BooleanBuilder): JPAQuery<Long> {
        return jpaQueryFactory
                .select(question.count())
                .from(question)
                .where(builder)
    }

    private fun answerAuthorFilter(author: Member, builder: BooleanBuilder) {
        builder.and(
                JPAExpressions
                        .selectOne()
                        .from(answer)
                        .where(answer.question.eq(question)
                                .and(answer.author.eq(author)))
                        .exists()
        )
    }

    fun getQuestions(builder: BooleanBuilder, pageable: Pageable): Page<Question> {
        val questionsQuery = createPostsQuery(builder)
        applySorting(pageable, questionsQuery)

        questionsQuery.offset(pageable.offset).limit(pageable.pageSize.toLong())

        val totalQuery = createTotalQuery(builder)

        return PageableExecutionUtils.getPage(questionsQuery.fetch(), pageable) {
            totalQuery.fetchOne() ?: 0L
        }
    }
}
