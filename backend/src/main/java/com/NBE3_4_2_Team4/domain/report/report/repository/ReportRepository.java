package com.NBE3_4_2_Team4.domain.report.report.repository;

import com.NBE3_4_2_Team4.domain.report.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByReporterId(Long reporterId, Pageable pageable);
}
