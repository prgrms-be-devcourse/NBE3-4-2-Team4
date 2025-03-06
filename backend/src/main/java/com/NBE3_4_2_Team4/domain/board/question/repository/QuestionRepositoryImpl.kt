package com.NBE3_4_2_Team4.domain.board.question.repository;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.standard.search.QuestionSearchKeywordType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import static com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer.answer;
import static com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion.question;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Question> findByKw(QuestionSearchKeywordType kwType, String kw, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(kw != null && !kw.isBlank())
            applyKeywordFilter(kwType, kw, builder);

        return getQuestions(builder, pageable);
    }

    @Override
    public Page<Question> findByAnswerAuthor(Member author, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(author != null)
            answerAuthorFilter(author, builder);

        return getQuestions(builder, pageable);
    }

    private void applyKeywordFilter(QuestionSearchKeywordType kwType, String kw, BooleanBuilder builder) {
        switch(kwType) {
            case kwType.TITLE -> builder.and(question.title.containsIgnoreCase(kw));
            case kwType.CONTENT -> builder.and(question.content.containsIgnoreCase(kw));
            case kwType.AUTHOR -> builder.and(question.author.nickname.containsIgnoreCase(kw));
            case kwType.ANSWER_CONTENT -> builder.and(hasAnswerContainingKeyword(kw));
            default -> builder.and(
                    question.title.containsIgnoreCase(kw)
                            .or(question.content.containsIgnoreCase(kw))
                            .or(question.author.nickname.containsIgnoreCase(kw))
                            .or(hasAnswerContainingKeyword(kw))
            );
        }
    }

    //answer content 내에 kw가 포함된 행이 하나라도 존재할 경우 true
    private BooleanExpression hasAnswerContainingKeyword(String kw) {
        return JPAExpressions
                .selectOne()
                .from(answer)
                .where(answer.question.eq(question)
                        .and(answer.answerContent.containsIgnoreCase(kw)))
                .exists();
    }

    private JPAQuery<Question> createPostsQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(question)
                .from(question)
                .where(builder);
    }

    private void applySorting(Pageable pageable, JPAQuery<Question> questionsQuery) {
        for(Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(question.getType(), question.getMetadata());
            questionsQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }
    }

    private JPAQuery<Long> createTotalQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(question.count())
                .from(question)
                .where(builder);
    }

    private void answerAuthorFilter(Member author, BooleanBuilder builder) {
        builder.and(
                JPAExpressions
                        .selectOne()
                        .from(answer)
                        .where(answer.question.eq(question)
                                .and(answer.author.eq(author)))
                        .exists()
        );
    }

    public Page<Question> getQuestions(BooleanBuilder builder, Pageable pageable) {
        JPAQuery<Question> questionsQuery = createPostsQuery(builder);
        applySorting(pageable, questionsQuery);

        questionsQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        JPAQuery<Long> totalQuery = createTotalQuery(builder);

        return PageableExecutionUtils.getPage(questionsQuery.fetch(), pageable, totalQuery::fetchOne);
    }
}
