package com.NBE3_4_2_Team4.domain.member.member.repository;

import com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer;
import com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion;
import com.NBE3_4_2_Team4.domain.member.member.dto.MemberDetailInfoResponseDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.entity.QMember;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class MemberQuerydsl extends QuerydslRepositorySupport {
    public MemberQuerydsl() {
        super(Member.class);
    }

    private final QMember m = QMember.member;
    private final QQuestion q = QQuestion.question;
    private final QAnswer a = QAnswer.answer;

    public MemberDetailInfoResponseDto getMemberDetailInfo(Member member) {
        return from(m)
                .leftJoin(q)
                .on(q.author.eq(m))
                .leftJoin(a)
                .on(a.author.eq(m))
                .select(Projections.constructor(MemberDetailInfoResponseDto.class,
                        m.nickname,
                        m.point,
                        q.count(),
                        a.count()))
                .distinct()
                .where(m.id.eq(member.getId()))
                .groupBy(m.nickname)
                .fetchOne();
    }
}
