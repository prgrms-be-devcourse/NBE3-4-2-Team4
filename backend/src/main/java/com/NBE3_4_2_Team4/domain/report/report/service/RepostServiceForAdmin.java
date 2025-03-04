package com.NBE3_4_2_Team4.domain.report.report.service;

import com.NBE3_4_2_Team4.domain.report.report.dto.ReportResponseDto;
import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportQuerydsl;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RepostServiceForAdmin {
    public ReportRepository reportRepository;
    public ReportQuerydsl reportQuerydsl;

    public Page<ReportResponseDto> findReportsByReporterId(Long reporterId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportQuerydsl.getReportsPageByReporterId(reporterId, pageable);
    }

    public Page<ReportResponseDto> findReportsByReportedId(Long reportedId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportQuerydsl.getReportsPageByReportedId(reportedId, pageable);
    }

    public void processReport(Report report) {
        report.setProcessed(true);
    }
}
