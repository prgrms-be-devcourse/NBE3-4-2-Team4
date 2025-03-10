package com.NBE3_4_2_Team4.domain.asset.main.validation.annotation

import com.NBE3_4_2_Team4.domain.asset.main.validation.validator.AssetHistoryReqValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [AssetHistoryReqValidator::class])
@Target(
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidAssetHistoryReq(
    val message: String = "Invalid date range",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

