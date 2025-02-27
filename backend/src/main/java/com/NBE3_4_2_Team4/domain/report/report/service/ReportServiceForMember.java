package com.NBE3_4_2_Team4.domain.report.report.service;

import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceForMember {
    private final ReportRepository reportRepository;

    public void saveNewReport() {}

    public Page<Report> findReportsByReporterId(Long reporterId) {
        return null;
    }

    public void updateReport() {}
}
