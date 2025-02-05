package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryReq;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryRes;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.PointClientException;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointService {
    private final MemberRepository memberRepository;
    private final PointHistoryService pointHistoryService;

    //포인트를 전송 로직
    @Transactional
    public void transfer(String fromUsername, String toUsername, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        if (fromUsername.equals(toUsername)) throw new PointClientException("자기 자신에게 송금할 수 없습니다");

        //보낸이 받는이 조회 & 락
        Member sender = memberRepository.findByUsernameWithLock(fromUsername)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", fromUsername)));

        Member recipient = memberRepository.findByUsernameWithLock(toUsername)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", toUsername)));

        //잔고 포인트 계산 & update
        long senderBalance = sender.getPoint() - amount;
        validateBalance(senderBalance);
        sender.setPoint(senderBalance);
        recipient.setPoint(recipient.getPoint() + amount);

        //기록 생성
        String correlationId = UUID.randomUUID().toString();
        pointHistoryService.createHistory(sender, recipient, amount * -1, pointCategory, correlationId);
        pointHistoryService.createHistory(recipient, sender, amount, pointCategory, correlationId);
    }

    //포인트 차감 로직
    @Transactional
    public void deductPoints(String from, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        Member member = memberRepository.findByUsernameWithLock(from)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", from)));

        long updatedBalance = member.getPoint() - amount;
        validateBalance(updatedBalance);

        member.setPoint(updatedBalance);

        String correlationId = UUID.randomUUID().toString();
        pointHistoryService.createHistory(member, null, amount * -1, pointCategory, correlationId);
    }

    //포인트 적립
    @Transactional
    public void accumulatePoints(String to, long amount, PointCategory pointCategory) {
        validateAmount(amount);
        Member member = memberRepository.findByUsernameWithLock(to)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", to)));

        member.setPoint(member.getPoint() + amount);

        String correlationId = UUID.randomUUID().toString();
        pointHistoryService.createHistory(member, null, amount, pointCategory, correlationId);
    }


    private void validateAmount(long amount) {
        if (amount <= 0) throw new PointClientException("거래금액이 0이나 음수가 될 수 없습니다");
    }

    private void validateBalance(long balance) {
        if (balance < 0) throw new PointClientException("출금 가능금액을 초과했습니다");
    }
}
