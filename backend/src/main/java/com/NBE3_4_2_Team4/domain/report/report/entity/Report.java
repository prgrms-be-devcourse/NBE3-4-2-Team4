package com.NBE3_4_2_Team4.domain.report.report.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.report.reportType.entity.ReportType;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseTime {
    @Setter
    @ManyToOne
    private ReportType reportType;

    @ManyToOne
    private Member reporter;

    @ManyToOne
    private Member reportedMember;

    @Setter
    private String content;

    @Setter
    private boolean processed;
}
