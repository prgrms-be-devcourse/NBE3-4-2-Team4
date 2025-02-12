package com.NBE3_4_2_Team4.domain.product.order.controller;

import com.NBE3_4_2_Team4.domain.product.order.service.ProductOrderService;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointService;
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto;
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.NBE3_4_2_Team4.domain.product.order.dto.ProductOrderRequestDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Tag(name = "OrderController", description = "상품 주문 API")
public class ProductOrderController {

    private final ProductService productService;
    private final PointService pointService;
    private final ProductOrderService productOrderService;

    @PutMapping("/{product_id}")
    @Operation(summary = "상품 구매 확정", description = "상품의 구매를 확정하고 상품을 구매한 유저의 포인트를 착감합니다.")
    public RsData<ProductResponseDto.GetItem> confirm(
            @PathVariable(name = "product_id") Long productId,
            @RequestBody @Validated PurchaseDetails request
    ) {

        // 해당하는 유저의 포인트를 상품 가격만큼 착감
        Long pointHistoryId = pointService.deductPoints(
                request.getUsername(),
                request.getAmount(),
                PointCategory.PURCHASE
        );

        // 상품 상태 변경
        productService.updateProduct(
                productId,
                ProductRequestDto.updateItem.builder()
                        .productSaleState(SaleState.SOLDOUT.name())
                        .build()
        );

        // 주문 생성
        productOrderService.createOrder(productId, pointHistoryId);

        return new RsData<>(
                "200-1",
                "%s의 포인트가 %d번 상품 가격 (%d) 만큼 착감되었습니다."
                        .formatted(request.getUsername(), productId, request.getAmount())
        );
    }
}
