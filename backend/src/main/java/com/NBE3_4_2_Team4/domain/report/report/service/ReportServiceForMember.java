package com.NBE3_4_2_Team4.domain.report.report.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportRequestDto;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportResponseDto;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportQuerydsl;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportRepository;
import com.NBE3_4_2_Team4.domain.report.reportType.entity.ReportType;
import com.NBE3_4_2_Team4.domain.report.reportType.repository.ReportTypeRepository;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceForMember {
    private final ReportRepository reportRepository;
    private final ReportTypeRepository reportTypeRepository;
    private final MemberRepository memberRepository;
    private final ReportQuerydsl reportQuerydsl;

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ServiceException("404-1", "Report not found"));
    }

    private ReportType getReportType(Long reportTypeId) {
        return reportTypeRepository.findById(reportTypeId)
                .orElseThrow(() -> new ServiceException("404-1", "Report Type not found"));
    }

    public void saveNewReport(Long reporterId, ReportRequestDto reportRequestDto) {
        Long reportTypeId = reportRequestDto.reportTypeId();
        ReportType reportType = getReportType(reportTypeId);

        Member reporter = getMember(reporterId);

        Long reportedId = reportRequestDto.reportedId();
        Member reportedMember = getMember(reportedId);

        String title = reportRequestDto.title();

        String content = reportRequestDto.content();

        reportRepository.save(Report.builder()
                .reportType(reportType)
                .reporter(reporter)
                .reportedMember(reportedMember)
                .title(title)
                .content(content)
                .build());
    }

    public Page<ReportResponseDto> findReportsByReporterId(Long reporterId, Integer page, Integer size) {
        Pageable pageable = Ut.pageable.makePageable(page, size);
        return reportQuerydsl.getReportsPageByReporterId(reporterId, pageable);
    }

    public void updateReport(ReportUpdateRequestDto reportUpdateRequestDto) {
        Long reportId = reportUpdateRequestDto.reportId();
        Report report = getReport(reportId);

        if (report.isProcessed()){
            throw new ServiceException("400-1", "Report already processed");
        }

        Long reportTypeId = reportUpdateRequestDto.reportTypeId();
        ReportType reportType = getReportType(reportTypeId);

        String content = reportUpdateRequestDto.content();

        report.setReportType(reportType);
        report.setContent(content);
    }
}
