package com.NBE3_4_2_Team4.domain.asset.point.service

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException
import com.NBE3_4_2_Team4.global.exceptions.PointClientException
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.standard.constants.PointConstants
import lombok.RequiredArgsConstructor
import org.apache.commons.lang3.tuple.Pair
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
@RequiredArgsConstructor
class PointService (
    private val memberRepository: MemberRepository,
            private val assetHistoryService: AssetHistoryService,
            private val adminAssetCategoryRepository: AdminAssetCategoryRepository
) : AssetService {


    //기록없이 포인트를 전송하는 메소드
    @Transactional
    private fun transferWithoutHistory(fromUsername: String, toUsername: String, amount: Long): Pair<Member, Member> {
        if (fromUsername == toUsername) throw PointClientException("자기 자신에게 송금할 수 없습니다")

        //보낸이 받는이 조회 & 락
        val sender: Member = memberRepository.findByUsernameWithLock(fromUsername)
            ?: throw MemberNotFoundException("${fromUsername}는 존재하지 않는 유저입니다")

        val recipient: Member = memberRepository.findByUsernameWithLock(toUsername)
            ?: throw MemberNotFoundException("${toUsername}s는 존재하지 않는 유저입니다")

        //잔고 포인트 계산 & update
        sender.point.subtract(amount)
        recipient.point.add(amount)

        return Pair.of(sender, recipient)
    }

    //포인트를 전송 + 기록
    @Transactional
    override fun transfer(fromUsername: String, toUsername: String, amount: Long, assetCategory: AssetCategory) {
        val (sender, recipient) = transferWithoutHistory(fromUsername, toUsername, amount)

        //기록 생성
        val correlationId = UUID.randomUUID().toString()

        assetHistoryService.run {
            createHistory(
                sender,
                recipient,
                amount * -1,
                assetCategory,
                null,
                AssetType.POINT,
                correlationId
            )
            createHistory(
                recipient,
                sender,
                amount,
                assetCategory,
                null,
                AssetType.POINT,
                correlationId
            )
        }
    }

    //포인트 차감 & 기록없음
    @Transactional
    private fun deductWithoutHistory(from: String, amount: Long): Member {
        val member: Member = memberRepository.findByUsernameWithLock(from)
            ?: throw MemberNotFoundException("${from}는 존재하지 않는 유저입니다")

        member.point.subtract(amount)
        return member
    }

    //포인트 차감 + 기록
    @Transactional
    override fun deduct(from: String, amount: Long, assetCategory: AssetCategory): Long {
        val member = deductWithoutHistory(from, amount)
        return assetHistoryService.createHistory(
            member,
            null,
            amount * -1,
            assetCategory,
            null,
            AssetType.POINT,
            UUID.randomUUID().toString()
        )
    }

    //포인트 적립, 기록없음
    @Transactional
    private fun accumulateWithoutHistory(to: String, amount: Long): Member {
        val member: Member = memberRepository.findByUsernameWithLock(to)
            ?: throw MemberNotFoundException("${to}는 존재하지 않는 유저입니다")

        member.point.add(amount)
        return member
    }

    //포인트 적립 + 기록
    @Transactional
    override fun accumulate(to: String, amount: Long, assetCategory: AssetCategory): Long {
        val member = accumulateWithoutHistory(to, amount)
        return assetHistoryService.createHistory(
            member,
            null,
            amount,
            assetCategory,
            null,
            AssetType.POINT,
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
            AssetType.POINT,
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
            AssetType.POINT,
            UUID.randomUUID().toString()
        )
    }

    @Transactional
    fun attend(memberId: Long?) {
        val today = LocalDate.now()

        //락으로 여러번 출석실행 방지
        val member: Member = memberRepository.findByIdWithLock(memberId)
            ?: throw MemberNotFoundException("존재하지 않는 유저입니다")

        //현재 날짜보다 전이면 포인트지급 & 마지막 출석일 업데이트, 아니면 에러
        if (!member.isFirstLoginToday) {
            throw PointClientException("출석실패: 이미 출석했습니다")
        }
        member.lastAttendanceDate = today
        accumulate(member.username, PointConstants.ATTENDANCE_POINT, AssetCategory.ATTENDANCE)
    }
}