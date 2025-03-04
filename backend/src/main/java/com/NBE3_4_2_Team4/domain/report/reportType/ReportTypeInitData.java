package com.NBE3_4_2_Team4.domain.report.reportType;

import com.NBE3_4_2_Team4.domain.report.reportType.entity.ReportType;
import com.NBE3_4_2_Team4.domain.report.reportType.repository.ReportTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ReportTypeInitData {
    private final ReportTypeRepository reportTypeRepository;

    private static final List<ReportType> REPORT_TYPES = List.of(
            ReportType.builder()
                    .name("스팸/광고성 글")
                    .build(),
            ReportType.builder()
                    .name("사기/허위 정보")
                    .build(),
            ReportType.builder()
                    .name("혐오 발언")
                    .build(),
            ReportType.builder()
                    .name("개인정보 침해")
                    .build(),
            ReportType.builder()
                    .name("기타")
                    .build()
    );

    @Autowired
    @Lazy
    private ReportTypeInitData self;

    @Bean
    public ApplicationRunner reportTypeInitDataApplicationRunner() {
        return _ -> {
            self.work();
        };
    }

    @Transactional
    public void work(){
        if(reportTypeRepository.count() != 0) {
            return;
        }
        reportTypeRepository.saveAll(REPORT_TYPES);
    }
}
