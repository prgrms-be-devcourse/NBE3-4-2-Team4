package com.NBE3_4_2_Team4.domain.product.order.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder;
import com.NBE3_4_2_Team4.domain.product.order.repository.ProductOrderRepository;
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

    @Transactional
    public Long createOrder(Long productId, Long pointHistoryId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 상품이 존재하지 않습니다."));

        AssetHistory assetHistory = assetHistoryRepository.findById(pointHistoryId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 포인트 내역이 존재하지 않습니다."));

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
