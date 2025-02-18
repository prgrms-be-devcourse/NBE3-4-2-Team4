package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.PointClientException;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointService {
    private final MemberRepository memberRepository;
    private final PointHistoryService pointHistoryService;

    @Transactional
    public Pair<Member, Member> transferWithoutHistory(String fromUsername, String toUsername, long amount) {
        Point.validateAmount(amount);
        if (fromUsername.equals(toUsername)) throw new PointClientException("자기 자신에게 송금할 수 없습니다");

        //보낸이 받는이 조회 & 락
        Member sender = memberRepository.findByUsernameWithLock(fromUsername)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", fromUsername)));

        Member recipient = memberRepository.findByUsernameWithLock(toUsername)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", toUsername)));


        Point senderPoint = sender.getPoint();
        Point recipientPoint = recipient.getPoint();

        //잔고 포인트 계산 & update
        senderPoint.subtract(amount);
        recipientPoint.add(amount);

        return Pair.of(sender, recipient);
    }

    //포인트를 전송 로직
    @Transactional
    public void transfer(String fromUsername, String toUsername, long amount, PointCategory pointCategory) {
        Pair<Member, Member> memberPair = transferWithoutHistory(fromUsername, toUsername, amount);

        Member sender = memberPair.getLeft();
        Member recipient = memberPair.getRight();

        //기록 생성
        String correlationId = UUID.randomUUID().toString();
        pointHistoryService.createHistory(sender, recipient, amount * -1, pointCategory, correlationId);
        pointHistoryService.createHistory(recipient, sender, amount, pointCategory, correlationId);
    }

    @Transactional
    public Member deductWithoutHistory(String from, long amount) {
        Point.validateAmount(amount);
        Member member = memberRepository.findByUsernameWithLock(from)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", from)));

        Point point = member.getPoint();
        point.subtract(amount);

        return member;
    }

    //포인트 차감 로직
    @Transactional
    public Long deduct(String from, long amount, PointCategory pointCategory) {
        Member member = deductWithoutHistory(from, amount);
        return pointHistoryService.createHistory(member, null, amount * -1, pointCategory, UUID.randomUUID().toString());
    }

    @Transactional
    public Member accumulateWithoutHistory(String to, long amount) {
        Point.validateAmount(amount);
        Member member = memberRepository.findByUsernameWithLock(to)
                .orElseThrow(() -> new MemberNotFoundException(String.format("%s는 존재하지 않는 유저입니다", to)));

        Point point = member.getPoint();
        point.add(amount);

        return member;
    }

    //포인트 적립
    @Transactional
    public Long accumulate(String to, long amount, PointCategory pointCategory) {
        Member member = accumulateWithoutHistory(to, amount);
        return pointHistoryService.createHistory(member, null, amount, pointCategory, UUID.randomUUID().toString());
    }

    @Transactional
    public void attend(Long memberId) {
        LocalDate today = LocalDate.now();

        //락으로 여러번 출석실행 방지
        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 유저입니다"));
        LocalDate lastAttendance = member.getLastAttendanceDate();

        //현재 날짜보다 전이면 포인트지급 & 마지막 출석일 업데이트, 아니면 에러
        if (
                lastAttendance != null && (lastAttendance.isEqual(today) || lastAttendance.isAfter(today))
        ) {
            throw new PointClientException("출석실패: 이미 출석했습니다");
        }
        member.setLastAttendanceDate(today);
        accumulate(member.getUsername(), PointConstants.ATTENDANCE_POINT, PointCategory.ATTENDANCE);
    }
}