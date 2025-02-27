package com.NBE3_4_2_Team4.domain.report.report.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.report.reportType.entity.ReportType;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Report extends BaseTime {
    @ManyToOne
    private ReportType reportType;

    @ManyToOne
    private Member reporter;

    @ManyToOne
    private Member reported;

    private String content;
}
