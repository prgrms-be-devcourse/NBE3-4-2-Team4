package com.NBE3_4_2_Team4.domain.asset.main.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq;
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryRes;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetHistoryService {
    private final AssetHistoryRepository assetHistoryRepository;

    @Transactional
    public Long createHistory(Member member, Member counterMember, long amount, AssetCategory assetCategory, AssetType assetType, String correlationId) {
        AssetHistory assetHistory = AssetHistory.builder()
                .assetCategory(assetCategory)
                .assetType(assetType)
                .amount(amount)
                .correlationId(correlationId)
                .member(member)
                .counterMember(counterMember)
                .build();

        assetHistoryRepository.save(assetHistory);
        return assetHistory.getId();
    }


    @Transactional(readOnly = true)
    public PageDto<AssetHistoryRes> getHistoryPageWithFilter(Member member, int size, AssetHistoryReq assetHistoryReq) {
        Pageable pageable = PageRequest.of(assetHistoryReq.getPage()-1, size, Sort.by("createdAt").descending());
        return new PageDto<AssetHistoryRes>(assetHistoryRepository
                .findByFilters(
                        member.getId(),
                        assetHistoryReq.getAssetCategory(),
                        assetHistoryReq.getAssetType(),
                        assetHistoryReq.getStartDateTime(),
                        assetHistoryReq.getEndDateTime(),
                        pageable)
                .map(AssetHistoryRes::from));
    }

    public long count() {
        return assetHistoryRepository.count();
    }
}
