package com.NBE3_4_2_Team4.domain.asset.main.validation.validator

import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq
import com.NBE3_4_2_Team4.domain.asset.main.validation.annotation.ValidAssetHistoryReq
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

//TODO: 에러메세지 안뜨는거 추후 수정
class AssetHistoryReqValidator : ConstraintValidator<ValidAssetHistoryReq, AssetHistoryReq> {
    override fun initialize(constraintAnnotation: ValidAssetHistoryReq) {
    }

    override fun isValid(assetHistoryReq: AssetHistoryReq, context: ConstraintValidatorContext): Boolean = with(assetHistoryReq) {
        if (endDate == null && startDate == null) return true

        return when {
            startDate == null || endDate == null -> context.invalidate("나머지 날짜를 입력하세요")
            endDate.isAfter(LocalDate.now()) -> context.invalidate("종료일이 현재 날짜를 초과할 수 없습니다")
            ChronoUnit.DAYS.between(startDate, endDate) < 30 -> context.invalidate("시작일과 종료일의 차이가 30일 이상이어야 합니다")
            else -> true
        }
    }

    private fun ConstraintValidatorContext.invalidate(message: String): Boolean {
        disableDefaultConstraintViolation()
        buildConstraintViolationWithTemplate(message).addConstraintViolation()
        return false
    }


}
