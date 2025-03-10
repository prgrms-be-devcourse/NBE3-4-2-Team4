package com.NBE3_4_2_Team4.domain.asset.cash.service

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import org.apache.commons.lang3.tuple.Pair
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CashService(
    private val memberRepository: MemberRepository,
    private val assetHistoryService: AssetHistoryService,
    private val adminAssetCategoryRepository: AdminAssetCategoryRepository
) : AssetService {

    @Transactional
    fun transferWithoutHistory(fromUsername: String, toUsername: String, amount: Long): Pair<Member, Member> {
        if (fromUsername == toUsername) throw ServiceException("401-1", "자기 자신에게 송금할 수 없습니다")

        //보낸이 받는이 조회 & 락
        val sender = memberRepository.findByUsernameWithLock(fromUsername)
            ?: throw MemberNotFoundException("${fromUsername}는 존재하지 않는 유저입니다")

        val recipient = memberRepository.findByUsernameWithLock(toUsername)
            ?: throw MemberNotFoundException("${fromUsername}는 존재하지 않는 유저입니다")

        //잔고 포인트 계산 & update
        sender.cash.subtract(amount)
        recipient.cash.add(amount)

        return Pair.of(sender, recipient)
    }

    //포인트를 전송 로직
    @Transactional
    override fun transfer(fromUsername: String, toUsername: String, amount: Long, assetCategory: AssetCategory) {
        val (sender, recipient) = transferWithoutHistory(fromUsername, toUsername, amount)
        val correlationId = UUID.randomUUID().toString()

        assetHistoryService.run {
            createHistory(sender, recipient, -amount, assetCategory, null, AssetType.CASH, correlationId)
            createHistory(recipient, sender, amount, assetCategory, null, AssetType.CASH, correlationId)
        }
    }

    @Transactional
    private fun deductWithoutHistory(from: String, amount: Long): Member {
        val member = memberRepository.findByUsernameWithLock(from)
            ?: throw MemberNotFoundException("${from}는 존재하지 않는 유저입니다")

        member.cash.subtract(amount)
        return member
    }

    //포인트 차감 로직
    @Transactional
    override fun deduct(from: String, amount: Long, assetCategory: AssetCategory): Long {
        val member = deductWithoutHistory(from, amount)
        return assetHistoryService.createHistory(
            member,
            null,
            amount * -1,
            assetCategory,
            null,
            AssetType.CASH,
            UUID.randomUUID().toString()
        )
    }

    @Transactional
    private fun accumulateWithoutHistory(to: String, amount: Long): Member {
        val member = memberRepository.findByUsernameWithLock(to)
            ?: throw MemberNotFoundException("${to}는 존재하지 않는 유저입니다")

        member.cash.add(amount)
        return member
    }

    //포인트 적립
    @Transactional
    override fun accumulate(to: String, amount: Long, assetCategory: AssetCategory): Long {
        val member = accumulateWithoutHistory(to, amount)
        return assetHistoryService.createHistory(
            member,
            null,
            amount,
            assetCategory,
            null,
            AssetType.CASH,
            UUID.randomUUID().toString()
        )
    }

    @Transactional
    override fun adminDeduct(from: String, amount: Long, admAstCategoryId: Long): Long {
        val adminAssetCategory = adminAssetCategoryRepository.findByIdOrNull(admAstCategoryId)
            ?: throw ServiceException("404-1", "category not found")

        val member = deductWithoutHistory(from, amount)
        return assetHistoryService.createHistory(
            member,
            null,
            amount * -1,
            AssetCategory.ADMIN,
            adminAssetCategory,
            AssetType.CASH,
            UUID.randomUUID().toString()
        )
    }

    @Transactional
    override fun adminAccumulate(to: String, amount: Long, admAstCategoryId: Long): Long {
        val adminAssetCategory = adminAssetCategoryRepository.findByIdOrNull(admAstCategoryId)
            ?: throw ServiceException("404-1", "category not found")

        val member = accumulateWithoutHistory(to, amount)
        return assetHistoryService.createHistory(
            member,
            null,
            amount,
            AssetCategory.ADMIN,
            adminAssetCategory,
            AssetType.CASH,
            UUID.randomUUID().toString()
        )
    }
}