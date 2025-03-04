package com.NBE3_4_2_Team4.domain.report.report.service;

import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportResponseDto;
import com.NBE3_4_2_Team4.domain.report.report.dto.reportType.ReportTypeRequestDto;
import com.NBE3_4_2_Team4.domain.report.report.dto.reportType.ReportTypeUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportQuerydsl;
import com.NBE3_4_2_Team4.domain.report.report.repository.ReportRepository;
import com.NBE3_4_2_Team4.domain.report.reportType.entity.ReportType;
import com.NBE3_4_2_Team4.domain.report.reportType.repository.ReportTypeRepository;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
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
    public ReportTypeRepository reportTypeRepository;

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


    public void createReportType(ReportTypeRequestDto reportTypeRequestDto) {
        String name = reportTypeRequestDto.name();

        if (reportTypeRepository.existsByName(name)) {
            throw new ServiceException("409-1", String.format("Report type with name %s already exists", name));
        }

        reportTypeRepository.save(ReportType.builder()
                .name(name)
                .build());
    }

    public void updateReportType(ReportTypeUpdateRequestDto reportTypeUpdateRequestDto) {
        Long id = reportTypeUpdateRequestDto.reportTypeId();

        String newName = reportTypeUpdateRequestDto.newName();
        if (reportTypeRepository.existsByName(newName)) {
            throw new ServiceException("409-1", String.format("Report type with name %s already exists", newName));
        }

        ReportType reportType = reportTypeRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", String.format("Report type with id %s not found", id)));

        reportType.setName(newName);
    }
}
