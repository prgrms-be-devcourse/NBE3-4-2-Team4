package com.NBE3_4_2_Team4.domain.payment.payment.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus

class PaymentRequestDto {

    data class ChargePayment(

        val impUid: String,
        val merchantUid: String,
        val amount: Long,
        val assetType: AssetType,
        val assetCategory: AssetCategory
    )

    data class CancelPayment(

        val paymentId: Long,
        val amount: Long,
        val reason: String,
        val assetType: AssetType,
        val assetCategory: AssetCategory
    )

    data class UpdatePayment(

        val status: PaymentStatus
    )
}