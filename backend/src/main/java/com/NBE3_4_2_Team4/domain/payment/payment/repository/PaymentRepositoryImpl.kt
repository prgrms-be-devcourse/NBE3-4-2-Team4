package com.NBE3_4_2_Team4.domain.payment.payment.repository

import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus
import com.NBE3_4_2_Team4.domain.payment.payment.entity.QPayment.payment
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
    em: EntityManager
) :PaymentRepositoryCustom {

    private val queryFactory = JPAQueryFactory(em)

    override fun findByMemberIdAndStatusKeyword(
        memberId: Long,
        paymentStatus: PaymentStatus,
        pageable: Pageable
    ): Page<Payment> {

        val builder = BooleanBuilder().apply {

            // 유저 조건 추가
            and(payment.assetHistory.member.id.eq(memberId))

            // 결제 상태 조건 추가
            if (paymentStatus != PaymentStatus.ALL) {
                and(payment.status.eq(paymentStatus))
            }
        }

        // 페이징 쿼리 생성
        val payments: List<Payment> = queryFactory
            .selectFrom(payment)
            .where(builder)
            .orderBy(payment.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // Count 쿼리 생성 (null 방지)
        val countQuery: JPAQuery<Long> = queryFactory
            .select(payment.count())
            .from(payment)
            .where(builder)

        return PageableExecutionUtils.getPage(payments, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }
}