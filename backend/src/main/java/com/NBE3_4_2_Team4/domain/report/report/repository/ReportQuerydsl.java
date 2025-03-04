package com.NBE3_4_2_Team4.domain.report.report.repository;

import com.NBE3_4_2_Team4.domain.member.member.entity.QMember;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportResponseDto;
import com.NBE3_4_2_Team4.domain.report.report.entity.QReport;
import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import com.NBE3_4_2_Team4.domain.report.reportType.entity.QReportType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportQuerydsl extends QuerydslRepositorySupport {
    private final QReport r = QReport.report;
    private final QMember m = QMember.member;
    private final QReportType reportType = QReportType.reportType;

    public ReportQuerydsl() {
        super(Report.class);
    }

    private JPQLQuery<ReportResponseDto> selectReportQuery(Pageable pageable){
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int offset = (page + 1) * size;

        return from(r)
                .innerJoin(m)
                .on(r.reporter.eq(m))
                .innerJoin(reportType)
                .on(r.reportType.eq(reportType))
                .select(Projections.constructor(ReportResponseDto.class,
                        r.reporter.nickname,
                        r.reportedMember.nickname,
                        reportType.name,
                        r.title,
                        r.content,
                        r.createdAt
                ))
                .limit(size)
                .offset(offset)
                .orderBy(r.createdAt.desc());
    }

    public Page<ReportResponseDto> getReportsPage(Pageable pageable){
        List<ReportResponseDto> content = selectReportQuery(pageable)
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    public Page<ReportResponseDto> getReportsPageByReporterId(Long reporterId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(r.reporter.id.eq(reporterId));

        List<ReportResponseDto> content = selectReportQuery(pageable)
                .where(builder)
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    public Page<ReportResponseDto> getReportsPageByReportedId(Long reportedId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(r.reportedMember.id.eq(reportedId));

        List<ReportResponseDto> content = selectReportQuery(pageable)
                .where(builder)
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }
}
