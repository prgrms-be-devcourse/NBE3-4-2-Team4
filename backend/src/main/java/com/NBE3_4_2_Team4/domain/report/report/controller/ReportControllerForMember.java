package com.NBE3_4_2_Team4.domain.report.report.controller;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportRequestDto;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportResponseDto;
import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.report.report.service.ReportServiceForMember;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.standard.base.Empty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportControllerForMember {
    private final ReportServiceForMember reportService;

    @PostMapping
    public RsData<Empty> createReport(
            @RequestBody ReportRequestDto reportRequestDto) {
        Member member = AuthManager.getNonNullMember();
        reportService.saveNewReport(member.getId(), reportRequestDto);
        return new RsData<>("201-1","report created");
    }

    @GetMapping
    public RsData<Page<ReportResponseDto>> getReportsPage(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ){
        Member member = AuthManager.getNonNullMember();
        Page<ReportResponseDto> reportsPage = reportService.findReportsByReporterId(member.getId(), page, size);
        return new RsData<>("201-1","report list", reportsPage);
    }

    @PatchMapping
    public RsData<Empty> updateReport(
            @RequestBody ReportUpdateRequestDto reportUpdateRequestDto
    ){
        reportService.updateReport(reportUpdateRequestDto);
        return new RsData<>("200-1","report updated");
    }
}
