package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryResponse;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
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
    private final MemberRepository memberRepository;

    @Transactional
    public void transfer(String fromUsername, String toUsername, long amount, PointCategory pointCategory) {
        Member sender = memberRepository.findByUsername(fromUsername)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        Member recipient = memberRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        long recipientBalance = recipient.getPoint() + amount;
        long senderBalance = sender.getPoint() - amount;
        validateBalance(senderBalance);

        sender.setPoint(senderBalance);
        recipient.setPoint(recipientBalance);

        String correlationId = UUID.randomUUID().toString();
        createHistory(sender, recipient, amount * -1, pointCategory, correlationId);
        createHistory(recipient, sender, amount, pointCategory, correlationId);
    }

    @Transactional
    public void withdraw(long from, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        Member member = memberRepository.findById(from)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        long updatedBalance = member.getPoint() - amount;
        validateBalance(updatedBalance);

        member.setPoint(updatedBalance);

        String correlationId = UUID.randomUUID().toString();
        createHistory(member, null, amount * -1, pointCategory, correlationId);
    }

    @Transactional
    public void deposit(long to, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        Member member = memberRepository.findById(to)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다"));

        member.setPoint(member.getPoint() + amount);

        String correlationId = UUID.randomUUID().toString();
        createHistory(member, null, amount, pointCategory, correlationId);
    }

    @Transactional
    public void createHistory(Member member, Member counterMember, long amount, PointCategory pointCategory, String correlationId) {
        PointHistory pointHistory = PointHistory.builder()
                .pointCategory(pointCategory)
                .amount(amount)
                .correlationId(correlationId)
                .member(member)
                .counterMember(counterMember)
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    @Transactional(readOnly = true)
    public PageDto<PointHistoryResponse> getHistoryPage(long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return new PageDto<PointHistoryResponse>(pointHistoryRepository
                .findByMemberId(memberId, pageable)
                .map(PointHistoryResponse::from));
    }

    private void validateAmount(long amount) {
        if (amount <= 0) throw new RuntimeException("거래금액이 0이나 음수가 될 수 없습니다");
    }

    private void validateBalance(long balance) {
        if (balance < 0) throw new RuntimeException("출금 가능금액을 초과했습니다");
    }
}
