package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryResponse;
import com.NBE3_4_2_Team4.domain.point.entity.Account;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.point.repository.AccountRepository;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointHistoryRepository pointHistoryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(long from, long to, long amount, PointCategory pointCategory) {
        Account sender = accountRepository.findById(from)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        Account recipient = accountRepository.findById(to)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        long recipientBalance = recipient.getBalance() + amount;
        long senderBalance = sender.getBalance() - amount;
        validateBalance(senderBalance);

        sender.setBalance(senderBalance);
        recipient.setBalance(recipientBalance);

        String correlationId = UUID.randomUUID().toString();
        createHistory(sender, recipient, amount * -1, pointCategory, correlationId);
        createHistory(recipient, sender, amount, pointCategory, correlationId);
    }

    @Transactional
    public void withdraw(long from, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        Account account = accountRepository.findById(from)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        long updatedBalance = account.getBalance() - amount;
        validateBalance(updatedBalance);

        account.setBalance(updatedBalance);

        String correlationId = UUID.randomUUID().toString();
        createHistory(account, null, amount * -1, pointCategory, correlationId);
    }

    @Transactional
    public void deposit(long to, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        Account account = accountRepository.findById(to)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        account.setBalance(account.getBalance() + amount);

        String correlationId = UUID.randomUUID().toString();
        createHistory(account, null, amount, pointCategory, correlationId);
    }

    @Transactional
    public void createHistory(Account account, Account counterAccount, long amount, PointCategory pointCategory, String correlationId) {
        PointHistory pointHistory = PointHistory.builder()
                .pointCategory(pointCategory)
                .amount(amount)
                .correlationId(correlationId)
                .account(account)
                .counterAccount(counterAccount)
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    @Transactional(readOnly = true)
    public Page<PointHistoryResponse> getHistoryPage(long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return pointHistoryRepository
                .findByAccountId(accountId, pageable)
                .map(PointHistoryResponse::from);
    }

    private void validateAmount(long amount) {
        if (amount <= 0) throw new RuntimeException("거래금액이 0이나 음수가 될 수 없습니다");
    }

    private void validateBalance(long balance) {
        if (balance < 0) throw new RuntimeException("출금 가능금액을 초과했습니다");
    }
}
