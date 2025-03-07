package com.NBE3_4_2_Team4.domain.payment.payment.repository;

import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.NBE3_4_2_Team4.domain.payment.payment.entity.QPayment.payment;

@Repository
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PaymentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Payment> findByMemberIdAndStatusKeyword(
            Long memberId,
            PaymentStatus paymentStatus,
            Pageable pageable
    ) {

        BooleanBuilder builder = new BooleanBuilder();

        // 유저 조건 추가
        builder.and(payment.assetHistory.member.id.eq(memberId));

        // 결제 상태에 따라 조건 추가
        if (paymentStatus != PaymentStatus.ALL) {
            builder.and(payment.status.eq(paymentStatus));
        }

        // 페이징 쿼리 생성
        List<Payment> payments = queryFactory
                .selectFrom(payment)
                .where(builder)
                .orderBy(payment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리 생성
        JPAQuery<Long> countQuery = queryFactory
                .select(payment.count())
                .from(payment)
                .where(builder);

        return PageableExecutionUtils.getPage(payments, pageable, countQuery::fetchOne);
    }
}