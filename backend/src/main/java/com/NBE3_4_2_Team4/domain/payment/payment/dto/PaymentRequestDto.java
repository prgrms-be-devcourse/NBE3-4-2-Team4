package com.NBE3_4_2_Team4.domain.payment.payment.dto;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import lombok.Builder;

public class PaymentRequestDto {

    @Builder
    public record ChargePayment(

            String impUid,

            String merchantUid,

            Long amount,

            AssetType assetType,

            AssetCategory assetCategory
    ){
    }

    @Builder
    public record CancelPayment(

            Long paymentId,

            long amount,

            String reason,

            AssetType assetType,

            AssetCategory assetCategory
    ) {
    }

    @Builder
    public record UpdatePayment(

            PaymentStatus status
    ) {
    }
}