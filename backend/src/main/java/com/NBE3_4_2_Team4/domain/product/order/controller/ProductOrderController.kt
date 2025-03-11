package com.NBE3_4_2_Team4.domain.product.order.controller;

import com.NBE3_4_2_Team4.domain.product.order.service.ProductOrderService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.NBE3_4_2_Team4.domain.product.order.dto.ProductOrderRequestDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Tag(name = "포인트 상품 주문 관리", description = "상품 주문 API")
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @PutMapping("/{product_id}")
    @Operation(summary = "상품 구매 확정", description = "상품의 구매를 확정하고 상품을 구매한 유저의 재화를 착감합니다.")
    public RsData<Empty> confirm(
            @PathVariable(name = "product_id") Long productId,
            @RequestBody @Validated PurchaseDetails request
    ) {

        productOrderService.createOrder(productId, request);

        return new RsData<>(
                "200-1",
                "상품 구매가 완료되었습니다. %s의 %s가 %d번 상품 가격 (%d) 만큼 착감되었습니다."
                        .formatted(
                                request.getUsername(),
                                request.getAssetType().toString(),
                                productId,
                                request.getAmount())
        );
    }
}
