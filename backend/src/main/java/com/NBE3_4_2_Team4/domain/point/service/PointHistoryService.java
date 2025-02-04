package com.NBE3_4_2_Team4.domain.point.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryReq;
import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryRes;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public Long createHistory(Member member, Member counterMember, long amount, PointCategory pointCategory, String correlationId) {
        PointHistory pointHistory = PointHistory.builder()
                .pointCategory(pointCategory)
                .amount(amount)
                .correlationId(correlationId)
                .member(member)
                .counterMember(counterMember)
                .build();

        pointHistoryRepository.save(pointHistory);
        return pointHistory.getId();
    }

    @Transactional(readOnly = true)
    public PageDto<PointHistoryRes> getHistoryPage(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());
        return new PageDto<PointHistoryRes>(pointHistoryRepository
                .findByMember(member, pageable)
                .map(PointHistoryRes::from));
    }

    @Transactional(readOnly = true)
    public PageDto<PointHistoryRes> getHistoryPageWithFilter(Member member, int size, PointHistoryReq pointHistoryReq) {
        Pageable pageable = PageRequest.of(pointHistoryReq.getPage()-1, size, Sort.by("createdAt").descending());
        return new PageDto<PointHistoryRes>(pointHistoryRepository
                .findByFilters(
                        member.getId(),
                        pointHistoryReq.getPointCategory(),
                        pointHistoryReq.getStartDateTime(),
                        pointHistoryReq.getEndDateTime(),
                        pageable)
                .map(PointHistoryRes::from));
    }

    public long count() {
        return pointHistoryRepository.count();
    }
}
