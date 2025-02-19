package com.NBE3_4_2_Team4.domain.asset.cash.service;

import com.NBE3_4_2_Team4.domain.asset.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.AssetService;
import com.NBE3_4_2_Team4.domain.asset.point.service.PointHistoryService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashService implements AssetService {
    private final MemberRepository memberRepository;
    private final PointHistoryService pointHistoryService;

    @Transactional
    @Override
    public Pair<Member, Member> transferWithoutHistory(String fromUsername, String toUsername, long amount) {
        Cash.validateAmount(amount);
        if (fromUsername.equals(toUsername)) throw new ServiceException("401-1", "자기 자신에게 송금할 수 없습니다");

        //보낸이 받는이 조회 & 락
        Member sender = memberRepository.findByUsernameWithLock(fromUsername)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", fromUsername)));

        Member recipient = memberRepository.findByUsernameWithLock(toUsername)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", toUsername)));


        Cash senderCash = sender.getCash();
        Cash recipientCash = recipient.getCash();

        //잔고 포인트 계산 & update
        senderCash.subtract(amount);
        recipientCash.add(amount);

        return Pair.of(sender, recipient);
    }

    //포인트를 전송 로직
    @Transactional
    @Override
    public void transfer(String fromUsername, String toUsername, long amount, AssetCategory assetCategory) {
        Pair<Member, Member> memberPair = transferWithoutHistory(fromUsername, toUsername, amount);

        Member sender = memberPair.getLeft();
        Member recipient = memberPair.getRight();

        //기록 생성
        String correlationId = UUID.randomUUID().toString();
        pointHistoryService.createHistory(sender, recipient, amount * -1, assetCategory, correlationId);
        pointHistoryService.createHistory(recipient, sender, amount, assetCategory, correlationId);
    }

    @Transactional
    @Override
    public Member deductWithoutHistory(String from, long amount) {
        Cash.validateAmount(amount);
        Member member = memberRepository.findByUsernameWithLock(from)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", from)));

        Cash cash = member.getCash();
        cash.subtract(amount);

        return member;
    }

    //포인트 차감 로직
    @Transactional
    @Override
    public Long deduct(String from, long amount, AssetCategory cashCategory) {
        Member member = deductWithoutHistory(from, amount);
        return pointHistoryService.createHistory(member, null, amount * -1, cashCategory, UUID.randomUUID().toString());
    }

    @Transactional
    @Override
    public Member accumulateWithoutHistory(String to, long amount) {
        Cash.validateAmount(amount);
        Member member = memberRepository.findByUsernameWithLock(to)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", to)));

        Cash cash = member.getCash();
        cash.add(amount);

        return member;
    }

    //포인트 적립
    @Transactional
    @Override
    public Long accumulate(String to, long amount, AssetCategory assetCategory) {
        Member member = accumulateWithoutHistory(to, amount);
        return pointHistoryService.createHistory(member, null, amount, assetCategory, UUID.randomUUID().toString());
    }
}
