package com.NBE3_4_2_Team4.domain.point.repository;


import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        pointHistoryRepository.deleteAll();

    }

    @Test
    void transfer1() {

    }

    @Test
    void createHistory() {
        PointHistory pointHistory = PointHistory.builder()
                .pointCategory(PointCategory.TRANSFER)
                .amount(100L)
                .build();

        PointHistory savedPointHistory = pointHistoryRepository.save(pointHistory);
        PointHistory foundPointHistory = pointHistoryRepository.findById(savedPointHistory.getId())
                .orElseThrow(() -> new AssertionError("PointHistory not found"));

        assertThat(foundPointHistory).isNotNull();
        assertThat(foundPointHistory.getPointCategory()).isEqualTo(PointCategory.TRANSFER);
        assertThat(foundPointHistory.getAmount()).isEqualTo(100L);
    }
}
