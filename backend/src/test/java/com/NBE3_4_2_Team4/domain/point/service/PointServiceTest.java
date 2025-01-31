package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.point.entity.Account;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.repository.AccountRepository;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class PointServiceTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account account1;
    private Account account2;
    private Long account1Id;
    private Long account2Id;

    @BeforeEach
    void setup() {
        account1 = Account.builder().balance(300L).build();
        account2 = Account.builder().balance(0L).build();

        accountRepository.save(account1);
        accountRepository.save(account2);
        account1Id = account1.getId();
        account2Id = account2.getId();
    }

    @Test
    void createHistoryTest() {
        pointService.transfer(account1Id, account2Id, 150L, PointCategory.TRANSFER);

        Account updatedAccount1 = accountRepository.findById(account1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        Account updatedAccount2 = accountRepository.findById(account2Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150L, updatedAccount1.getBalance(), "Account1 balance should be 150 less");
        assertEquals(150L, updatedAccount2.getBalance(), "Account2 balance should be 150 more");
    }
}
