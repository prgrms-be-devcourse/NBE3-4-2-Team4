package com.NBE3_4_2_Team4.domain.point.validation.annotation;

import com.NBE3_4_2_Team4.domain.point.validation.validator.PointHistoryReqValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PointHistoryReqValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPointHistoryReq {
    String message() default "Invalid date range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
