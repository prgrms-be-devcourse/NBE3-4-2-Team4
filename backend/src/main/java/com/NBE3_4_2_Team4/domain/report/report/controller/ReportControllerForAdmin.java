package com.NBE3_4_2_Team4.domain.report.report.controller;

import com.NBE3_4_2_Team4.domain.report.report.dto.report.ReportResponseDto;
import com.NBE3_4_2_Team4.domain.report.report.service.ReportServiceForAdmin;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reports")
public class ReportControllerForAdmin {
    private final ReportServiceForAdmin reportService;

    @GetMapping
    public RsData<Page<ReportResponseDto>> getAllReports(
            @RequestParam(name = "reporterId", required = false) Long reporterId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new RsData<>("200-1", "reports", reportService.findAllReports(page, size));
    }

    @GetMapping("/reporters/{reporterId}")
    public RsData<Page<ReportResponseDto>> getReportsByReporterId(
            @PathVariable(name = "reporterId") Long reporterId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ){
        return new RsData<>("200-1", "reports", reportService.findReportsByReporterId(reporterId, page, size));
    }

    @GetMapping("/reporters/{reportedMemberId}")
    public RsData<Page<ReportResponseDto>> getReportsByReportedMemberId(
            @PathVariable(name = "reportedMemberId") Long reportedMemberId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ){
        return new RsData<>("200-1", "reports", reportService.findReportsByReporterId(reportedMemberId, page, size));
    }
}
