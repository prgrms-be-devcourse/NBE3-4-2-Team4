package com.NBE3_4_2_Team4.domain.asset.main.service

import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryRes
import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.standard.dto.PageDto
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssetHistoryService (
    private val assetHistoryRepository: AssetHistoryRepository
){


    @Transactional
    fun createHistory(
        member: Member,
        counterMember: Member?,
        amount: Long,
        assetCategory: AssetCategory,
        adminAssetCategory: AdminAssetCategory?,
        assetType: AssetType,
        correlationId: String
    ): Long {
        val assetHistory =
            AssetHistory(
                member,
                amount,
                assetType,
                assetCategory,
                correlationId,
                adminAssetCategory,
                counterMember
            )

        val assetHistoryId = assetHistoryRepository.save(assetHistory).id
            ?: throw ServiceException("assetHistory id가 null 입니다", "500-1")

        return assetHistoryId
    }


    @Transactional(readOnly = true)
    fun getHistoryPageWithFilter(
        member: Member,
        size: Int,
        assetHistoryReq: AssetHistoryReq
    ): PageDto<AssetHistoryRes> {
        val pageable: Pageable = PageRequest.of(assetHistoryReq.page - 1, size, Sort.by("createdAt").descending())
        return PageDto(assetHistoryRepository
            .findByFilters(
                member.id,
                assetHistoryReq.assetCategory,
                assetHistoryReq.assetType,
                assetHistoryReq.startDateTime,
                assetHistoryReq.endDateTime,
                pageable
            )
            .map(AssetHistoryRes::from))
    }

    fun count(): Long {
        return assetHistoryRepository.count()
    }
}
