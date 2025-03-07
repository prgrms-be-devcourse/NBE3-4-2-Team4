package com.NBE3_4_2_Team4.domain.asset.main.validation.annotation;

import com.NBE3_4_2_Team4.domain.asset.main.validation.validator.AssetHistoryReqValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AssetHistoryReqValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAssetHistoryReq {
    String message() default "Invalid date range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
