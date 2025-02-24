package com.NBE3_4_2_Team4.domain.asset.main.repository;



import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    Page<AssetHistory> findByMember(Member member, Pageable pageable);

    @Query("""
    SELECT a FROM AssetHistory a
    WHERE (:assetCategory IS NULL OR a.assetCategory = :assetCategory)
    AND
    (:assetType IS NULL OR a.assetType = :assetType)
    AND (
        (:startDateTime IS NULL OR :endDateTime IS NULL) OR
        (a.createdAt BETWEEN :startDateTime AND :endDateTime)
    )
    AND (a.member.id = :memberId)
    """)
    Page<AssetHistory> findByFilters(
            @Param("memberId") Long memberId,
            @Param("assetCategory") AssetCategory assetCategory,
            @Param("assetType") AssetType assetType,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}
