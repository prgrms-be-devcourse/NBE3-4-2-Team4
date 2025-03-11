package com.NBE3_4_2_Team4.domain.product.order.controller

import com.NBE3_4_2_Team4.domain.product.order.dto.ProductOrderRequestDto.PurchaseDetails
import com.NBE3_4_2_Team4.domain.product.order.service.ProductOrderService
import com.NBE3_4_2_Team4.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/order")
@Tag(name = "포인트 상품 주문 관리", description = "상품 주문 API")
class ProductOrderController(

    private val productOrderService: ProductOrderService
) {

    @PutMapping("/{product_id}")
    @Operation(summary = "상품 구매 확정", description = "상품의 구매를 확정하고 상품을 구매한 유저의 재화를 착감합니다.")
    fun confirm(
        @PathVariable(name = "product_id") productId: Long,
        @RequestBody @Valid request: PurchaseDetails
    ): RsData<Unit> {

        productOrderService.createOrder(productId, request)

        return RsData(
            "200-1",
            "상품 구매가 완료되었습니다. " +
                    "${request.username}의 ${request.assetType}가 ${productId}번 상품 가격 (${request.amount}) 만큼 착감되었습니다."
        )
    }
}