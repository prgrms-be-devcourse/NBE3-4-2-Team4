package com.NBE3_4_2_Team4.domain.asset.main.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
public interface AssetService {
    public void transfer(String fromUsername, String toUsername, long amount, AssetCategory assetCategory);
    public Long deduct(String from, long amount, AssetCategory assetCategory);
    public Long accumulate(String to, long amount, AssetCategory assetCategory);
    public Long adminDeduct(String from, long amount, long admAstCategoryId);
    public Long adminAccumulate(String to, long amount, long admAstCategoryId);
}
