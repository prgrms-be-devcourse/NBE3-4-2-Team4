package com.NBE3_4_2_Team4.domain.asset.point.repository;



import com.NBE3_4_2_Team4.domain.asset.AssetCategory;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.asset.point.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findByMember(Member member, Pageable pageable);

    @Query("""
    SELECT p FROM PointHistory p
    WHERE (:assetCategory IS NULL OR p.assetCategory = :assetCategory)
    AND (
        (:startDateTime IS NULL OR :endDateTime IS NULL) OR
        (p.createdAt BETWEEN :startDateTime AND :endDateTime)
    )
    AND (p.member.id = :memberId)
    """)
    Page<PointHistory> findByFilters(
            @Param("memberId") Long memberId,
            @Param("pointCategory") AssetCategory assetCategory,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}
