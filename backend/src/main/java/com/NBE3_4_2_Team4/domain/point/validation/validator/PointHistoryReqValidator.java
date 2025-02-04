package com.NBE3_4_2_Team4.domain.point.validation.validator;

import com.NBE3_4_2_Team4.domain.point.dto.PointHistoryReq;
import com.NBE3_4_2_Team4.domain.point.validation.annotation.ValidPointHistoryReq;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PointHistoryReqValidator implements ConstraintValidator<ValidPointHistoryReq, PointHistoryReq> {
    @Override
    public void initialize(ValidPointHistoryReq constraintAnnotation) {
    }

    @Override
    public boolean isValid(PointHistoryReq pointHistoryReq, ConstraintValidatorContext context) {

        if (pointHistoryReq.getEndDate() == null && pointHistoryReq.getStartDate() == null) return true;

        if (pointHistoryReq.getStartDate() == null || pointHistoryReq.getEndDateTime() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("나머지 날짜를 입력하세요")
                    .addConstraintViolation();
            return false;
        }

        // 현재 날짜 체크
        if (pointHistoryReq.getEndDate().isAfter(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("종료일이 현재 날짜를 초과할 수 없습니다")
                    .addConstraintViolation();
            return false;
        }

        // 30일 이상 차이 체크
        long daysBetween = ChronoUnit.DAYS.between(pointHistoryReq.getStartDate(), pointHistoryReq.getEndDate());
        if (daysBetween < 30) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("시작일과 종료일의 차이가 30일 이상이어야 합니다")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
