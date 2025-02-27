package com.NBE3_4_2_Team4.domain.report.report.service;

import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepostServiceForAdmin {
    public ReportRepository reportRepository;

    public Page<Report> findReportsByReporterId(Long reporterId) {
        return null;
    }

    public Page<Report> findReportsByReportedMemberId(Long reportedMemberId) {
        return null;
    }

    public void processReport(Report report) {
        report.setProcessed(true);
    }
}
