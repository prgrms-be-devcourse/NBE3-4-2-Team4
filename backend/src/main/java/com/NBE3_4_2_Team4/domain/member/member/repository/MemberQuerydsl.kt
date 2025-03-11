package com.NBE3_4_2_Team4.domain.member.member.repository

import com.NBE3_4_2_Team4.domain.asset.main.entity.QAssetHistory
import com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer
import com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.QOAuth2RefreshToken
import org.springframework.stereotype.Repository
import com.NBE3_4_2_Team4.domain.member.member.dto.MemberDetailInfoResponseDto
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.QMember
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

@Repository
class MemberQuerydsl(private val queryFactory: JPAQueryFactory) : QuerydslRepositorySupport(Member::class.java) {

    private val m: QMember = QMember.member
    private val q: QQuestion = QQuestion.question
    private val a: QAnswer = QAnswer.answer
    private val ast: QAssetHistory = QAssetHistory.assetHistory
    private val t: QOAuth2RefreshToken = QOAuth2RefreshToken.oAuth2RefreshToken

    fun getMemberDetailInfo(member: Member): MemberDetailInfoResponseDto? {
        return queryFactory
            .select(
                Projections.constructor(
                    MemberDetailInfoResponseDto::class.java,
                    m.username,
                    m.nickname,
                    m.point,
                    m.cash,
                    q.count(),
                    a.count(),
                    m.emailAddress,
                    m.emailVerified
                )
            )
            .from(m)
            .leftJoin(q).on(q.author.eq(m))
            .leftJoin(a).on(a.author.eq(m))
            .where(m.id.eq(member.id))
            .groupBy(m.nickname)
            .distinct()
            .fetchOne()
    }

    fun deleteMember(memberId: Long) {
        queryFactory.update(q)
            .setNull(q.author.id)
            .where(q.author.id.eq(memberId))
            .execute()

        queryFactory.update(a)
            .setNull(a.author.id)
            .where(a.author.id.eq(memberId))
            .execute()

        queryFactory.update(ast)
            .setNull(ast.member.id)
            .where(ast.member.id.eq(memberId))
            .execute()

        queryFactory.delete(t)
            .where(t.member.id.eq(memberId))
            .execute()

        queryFactory.delete(m)
            .where(m.id.eq(memberId))
            .execute()
    }
}
