package com.NBE3_4_2_Team4.domain.product.order.service

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService
import com.NBE3_4_2_Team4.domain.product.order.dto.ProductOrderRequestDto.PurchaseDetails
import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder
import com.NBE3_4_2_Team4.domain.product.order.repository.ProductOrderRepository
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductOrderService(

    private val productOrderRepository: ProductOrderRepository,
    private val assetHistoryRepository: AssetHistoryRepository,
    private val productRepository: ProductRepository,
    private val assetServiceFactory: AssetServiceFactory
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createOrder(
        productId: Long,
        purchaseDetails: PurchaseDetails
    ): Long {

        // 상품 조회
        val product = productRepository.findById(productId)
            .orElseThrow { ServiceException("404-1", "해당 상품이 존재하지 않습니다.") }

        // 재화 착감
        val assetService: AssetService = assetServiceFactory.getService(purchaseDetails.assetType)
        val assetHistoryId = assetService.deduct(
            purchaseDetails.username,
            purchaseDetails.amount,
            purchaseDetails.assetCategory
        )

        // 재화 내역 조회
        val assetHistory = assetHistoryRepository.findById(assetHistoryId)
            .orElseThrow { ServiceException("404-2", "해당 재화 내역이 존재하지 않습니다.") }

        // 주문 생성
        val savedOrder = productOrderRepository.save(
            ProductOrder(
                product = product,
                assetHistory = assetHistory
            )
        )

        logger.info { "Order Id [${savedOrder.id}] is saved." }

        return savedOrder.id
    }
}