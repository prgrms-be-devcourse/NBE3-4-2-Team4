package com.NBE3_4_2_Team4.domain.asset.point.dto;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AssetHistoryRes {
    private final Long amount;
    private final LocalDateTime createdAt;
    private final String counterAccountUsername;
    private final String assetCategory;

    public static AssetHistoryRes from(AssetHistory assetHistory) {
        Member counterMember = assetHistory.getCounterMember();
        String counterMemberUsername = (counterMember != null)
                ? counterMember.getUsername() : "";

        return AssetHistoryRes.builder()
                .amount(assetHistory.getAmount())
                .createdAt(assetHistory.getCreatedAt())
                .counterAccountUsername(counterMemberUsername)
                .assetCategory(assetHistory.getAssetCategory().getDisplayName())
                .build();
    }
}
