package com.NBE3_4_2_Team4.domain.product.order.service;

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService;
import com.NBE3_4_2_Team4.domain.product.order.dto.ProductOrderRequestDto.PurchaseDetails;
import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder;
import com.NBE3_4_2_Team4.domain.product.order.repository.ProductOrderRepository;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final ProductRepository productRepository;
    private final AssetServiceFactory assetServiceFactory;

    @Transactional
    public Long createOrder(Long productId, PurchaseDetails purchaseDetails) {

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 상품이 존재하지 않습니다."));

        // 재화 착감
        AssetService assetService = assetServiceFactory.getService(purchaseDetails.getAssetType());
        Long assetHistoryId = assetService.deduct(
                purchaseDetails.getUsername(),
                purchaseDetails.getAmount(),
                purchaseDetails.getAssetCategory()
        );

        // 재화 내역 조회
        AssetHistory assetHistory = assetHistoryRepository.findById(assetHistoryId)
                .orElseThrow(() -> new ServiceException("404-2", "해당 재화 내역이 존재하지 않습니다."));

        // 주문 생성
        ProductOrder saved = productOrderRepository.save(
                ProductOrder.builder()
                        .orderTime(LocalDateTime.now())
                        .product(product)
                        .assetHistory(assetHistory)
                        .build()
        );

        log.info("Order Id [{}] is saved.", saved.getId());

        return saved.getId();
    }
}
