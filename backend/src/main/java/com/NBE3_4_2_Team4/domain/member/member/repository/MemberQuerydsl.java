package com.NBE3_4_2_Team4.domain.member.member.repository;

import com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer;
import com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.QOAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.member.dto.MemberDetailInfoResponseDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.entity.QMember;
import com.NBE3_4_2_Team4.domain.asset.point.entity.QPointHistory;
import com.querydsl.core.types.Projections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Slf4j
@Repository
public class MemberQuerydsl extends QuerydslRepositorySupport {
    public MemberQuerydsl() {
        super(Member.class);
    }

    private final QMember m = QMember.member;
    private final QQuestion q = QQuestion.question;
    private final QAnswer a = QAnswer.answer;
    private final QPointHistory p = QPointHistory.pointHistory;
    private final QOAuth2RefreshToken t = QOAuth2RefreshToken.oAuth2RefreshToken;

    public MemberDetailInfoResponseDto getMemberDetailInfo(Member member) {
        return from(m)
                .leftJoin(q)
                .on(q.author.eq(m))
                .leftJoin(a)
                .on(a.author.eq(m))
                .select(Projections.constructor(MemberDetailInfoResponseDto.class,
                        m.username,
                        m.nickname,
                        m.point,
                        q.count(),
                        a.count()))
                .distinct()
                .where(m.id.eq(member.getId()))
                .groupBy(m.nickname)
                .fetchOne();
    }

    public void deleteMember(long memberId) {
        update(q)
                .setNull(q.author.id)
                .where(q.author.id.eq(memberId))
                .execute();
        update(a)
                .setNull(a.author.id)
                .where(a.author.id.eq(memberId))
                .execute();

        update(p)
                .setNull(p.member.id)
                .where(p.member.id.eq(memberId))
                .execute();

        delete(t)
                .where(t.member.id.eq(memberId))
                .execute();

        delete(m)
                .where(m.id.eq(memberId))
                .execute();
    }

    public void updateLastLoginDate(Member member, LocalDate today) {
        update(m)
                .set(m.lastAttendanceDate, today)
                .execute();
    }
}
