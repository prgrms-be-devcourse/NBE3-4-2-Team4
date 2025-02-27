package com.NBE3_4_2_Team4.domain.report.report.service;

import com.NBE3_4_2_Team4.domain.report.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepostServiceForAdmin {
    public ReportRepository reportRepository;
}
