package com.NBE3_4_2_Team4.domain.asset.main.validation.validator;

import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq;
import com.NBE3_4_2_Team4.domain.asset.main.validation.annotation.ValidAssetHistoryReq;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AssetHistoryReqValidator implements ConstraintValidator<ValidAssetHistoryReq, AssetHistoryReq> {
    @Override
    public void initialize(ValidAssetHistoryReq constraintAnnotation) {
    }

    @Override
    public boolean isValid(AssetHistoryReq assetHistoryReq, ConstraintValidatorContext context) {

        if (assetHistoryReq.getEndDate() == null && assetHistoryReq.getStartDate() == null) return true;

        if (assetHistoryReq.getStartDate() == null || assetHistoryReq.getEndDateTime() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("나머지 날짜를 입력하세요")
                    .addConstraintViolation();
            return false;
        }

        // 현재 날짜 체크
        if (assetHistoryReq.getEndDate().isAfter(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("종료일이 현재 날짜를 초과할 수 없습니다")
                    .addConstraintViolation();
            return false;
        }

        // 30일 이상 차이 체크
        long daysBetween = ChronoUnit.DAYS.between(assetHistoryReq.getStartDate(), assetHistoryReq.getEndDate());
        if (daysBetween < 30) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("시작일과 종료일의 차이가 30일 이상이어야 합니다")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
