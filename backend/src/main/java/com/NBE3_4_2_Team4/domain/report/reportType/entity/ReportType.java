package com.NBE3_4_2_Team4.domain.report.reportType.entity;

import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportType extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
}
