package com.NBE3_4_2_Team4.domain.product.product.service

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory
import com.NBE3_4_2_Team4.domain.product.category.repository.ProductCategoryRepository
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.UpdateItem
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.WriteItem
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.*
import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState
import com.NBE3_4_2_Team4.domain.product.saleState.repository.ProductSaleStateRepository
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.standard.dto.PageDto
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType
import com.NBE3_4_2_Team4.standard.util.Ut
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(

    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productSaleStateRepository: ProductSaleStateRepository
) {

    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun countProducts(): Long = productRepository.count()

    @Transactional(readOnly = true)
    fun getProducts(
    ): List<GetItem> {

        val products = productRepository.findAll()

        logger.info { "[${products.size}] Products are found." }

        return products.map {
            GetItem(
                it, makeFullCategory(it), it.saleState.name
            )
        }
    }

    @Transactional(readOnly = true)
    fun getProducts(
        page: Int,
        pageSize: Int,
        searchKeywordType: ProductSearchKeywordType,
        searchKeyword: String,
        categoryKeyword: String,
        saleStateKeyword: String
    ): PageDto<GetItem> {

        val pageable: Pageable = Ut.pageable.makePageable(page, pageSize)
        val saleState = SaleState.fromString(saleStateKeyword.uppercase())

        val products = productRepository.findByKeyword(
            searchKeywordType, searchKeyword, categoryKeyword, saleState, pageable
        )

        logger.info { "[${products.content.size}] Products are found with paging." }

        return PageDto(products.map {
            GetItem(
                it, makeFullCategory(it), it.saleState.name
            )
        })
    }

    @Transactional(readOnly = true)
    fun getProductsByCategoryKeyword(
        categoryKeyword: String
    ): GetItemsByKeyword {

        // keyword 포함되는 categories 가져오기
        val categories = productCategoryRepository.findByNameContainingOrderByIdAsc(categoryKeyword)
        if (categories.isEmpty()) {
            logger.warn { "Category keyword [$categoryKeyword] not found." }
            return GetItemsByKeyword(categoryKeyword, emptyList())
        }

        // categories 내 최하위 카테고리를 모두 찾아서 TreeMap에 저장
        val leafCategories = mutableSetOf<Long>()
        categories.forEach {
            saveChildCategories(it, leafCategories)
        }

        // 최하위 카테고리에 해당하는 상품 조회
        val products = productRepository.findByCategoryIdIn(leafCategories)

        logger.info { "[${products.size}] Products are found by category keyword [$categoryKeyword]." }

        return GetItemsByKeyword(categoryKeyword, products.map {
            GetItem(
                it, makeFullCategory(it), it.saleState.name
            )
        })
    }

    @Transactional(readOnly = true)
    fun getProductsByCategoryKeyword(
        categoryKeyword: String, page: Int, pageSize: Int
    ): PageDtoWithKeyword<GetItem> {

        // keyword 포함되는 categories 가져오기
        val categories = productCategoryRepository.findByNameContainingOrderByIdAsc(categoryKeyword)
        if (categories.isEmpty()) {
            logger.warn { "Category keyword [$categoryKeyword] not found." }
            return PageDtoWithKeyword(Page.empty(), categoryKeyword)
        }

        // categories 내 최하위 카테고리를 모두 찾아서 TreeMap에 저장
        val leafCategories = mutableSetOf<Long>()
        categories.forEach {
            saveChildCategories(it, leafCategories)
        }

        val pageable = Ut.pageable.makePageable(page, pageSize)

        // 최하위 카테고리에 해당하는 상품 조회
        val products = productRepository.findByCategoryIdIn(leafCategories, pageable)

        logger.info { "[${products.content.size}] Products are found with paging by category keyword [$categoryKeyword]." }

        return PageDtoWithKeyword(
            products.map {
                GetItem(
                    it, makeFullCategory(it), it.saleState.name
                )
            }, categoryKeyword
        )
    }

    @Transactional(readOnly = true)
    fun getProductsBySaleStateKeyword(
        saleStateKeyword: String
    ): GetItemsByKeyword {

        // 판매 상태 파라미터 유효성 체크
        val saleState = SaleState.fromString(saleStateKeyword.uppercase())

        val products = productRepository.findBySaleStateLike(saleState)

        logger.info { "[${products.size}] Products are found by Sale State keyword [$saleStateKeyword]." }

        return GetItemsByKeyword(
            saleStateKeyword, products.map {
                GetItem(
                    it, makeFullCategory(it), it.saleState.name
                )
            })
    }

    @Transactional(readOnly = true)
    fun getProductsBySaleStateKeyword(
        saleStateKeyword: String, page: Int, pageSize: Int
    ): PageDtoWithKeyword<GetItem> {

        // 판매 상태 파라미터 유효성 체크
        val saleState = SaleState.fromString(saleStateKeyword.uppercase())

        val pageable: Pageable = Ut.pageable.makePageable(page, pageSize)

        val products = productRepository.findBySaleStateLike(saleState, pageable)

        logger.info { "[${products.content.size}] Products are found with paging by Sale State keyword [$saleStateKeyword]." }

        return PageDtoWithKeyword(
            products.map {
                GetItem(
                    it, makeFullCategory(it), it.saleState.name
                )
            }, saleStateKeyword
        )
    }

    @Transactional(readOnly = true)
    fun getProduct(
        productId: Long
    ): GetItem {

        val product = findById(productId)

        return GetItem(
            product, makeFullCategory(product), product.saleState.name
        )
    }

    @Transactional(readOnly = true)
    fun findById(
        productId: Long
    ): Product {

        return productRepository.findById(productId).orElseThrow { ServiceException("404-2", "해당 상품이 존재하지 않습니다.") }
            .also { logger.info { "Product Id [$productId] is found." } }
    }

    @Transactional
    fun writeProduct(
        request: WriteItem
    ): GetItem {

        val categories = splitAndSaveByFullCategory(request.productCategory)

        val saleState = findAndSaveBySaleState(request.productSaleState) ?: throw ServiceException(
            "404-1", "유효하지 않는 판매 상태 키워드 입니다."
        )

        val product = productRepository.save(request.toProduct(categories.last(), saleState))

        logger.info { "Product Id [${product.id}] is saved." }

        return GetItem(
            product, makeFullCategory(product), product.saleState.name
        )
    }

    @Transactional
    fun updateProduct(
        productId: Long, request: UpdateItem
    ): GetItem {

        val product =
            productRepository.findById(productId).orElseThrow { ServiceException("404-2", "해당 상품이 존재하지 않습니다.") }

        request.productName?.let {
            logger.debug { "Product Name [$it] is update target." }
            product.updateName(it)
        }

        request.productPrice?.let {
            logger.debug { "Product Price [$it] is update target." }
            product.updatePrice(it)
        }

        request.productDescription?.let {
            logger.debug { "Product Description [$it] is update target." }
            product.updateDescription(it)
        }

        request.productImageUrl?.let {
            logger.debug { "Product Image Url [$it] is update target." }
            product.updateImageUrl(it)
        }

        request.productCategory?.let {
            logger.debug { "Product Category [$it] is update target." }
            val categories = splitAndSaveByFullCategory(it)
            product.updateCategory(categories.lastOrNull() ?: throw ServiceException("404-3", "유효하지 않은 카테고리입니다."))
        }

        request.productSaleState?.let {
            logger.debug { "Product Sale State [$it] is update target." }
            val saleState = findAndSaveBySaleState(it) ?: throw ServiceException("404-1", "유효하지 않는 판매 상태 키워드 입니다.")
            product.updateSaleState(saleState)
        }

        val updatedProduct = productRepository.save(product)

        logger.info { "Product Id [${updatedProduct.id}] is updated." }

        return GetItem(
            updatedProduct, makeFullCategory(updatedProduct), updatedProduct.saleState.name
        )
    }

    @Transactional
    fun deleteProduct(
        productId: Long
    ) {

        val deleteProduct =
            productRepository.findById(productId).orElseThrow { ServiceException("404-2", "해당 상품이 존재하지 않습니다.") }

        productRepository.delete(deleteProduct)
        logger.info { "Product Id [${deleteProduct.id}] is deleted." }
    }

    @Transactional(readOnly = true)
    fun findCategoriesName(): List<String> {

        val categories = productCategoryRepository.findAll()
        val categoriesName = mutableSetOf<String>()

        categories.forEach { saveParentCategoriesName(it, categoriesName) }

        return categoriesName.toList()
    }

    @Transactional(readOnly = true)
    fun findSaleStateNames(): List<String> {

        return productSaleStateRepository.findAll().map { it.name.name }
    }

    private fun makeFullCategory(
        product: Product
    ): String {

        val categories = mutableListOf<String>()
        var category: ProductCategory? = product.category

        // 상위 카테고리가 없을 때까지 카테고리 추가
        while (category != null) {
            categories.add(0, category.name)
            category = category.parent
        }

        // 마지막 '/' 문자 제거
        return categories.joinToString("/")
    }

    private fun saveChildCategories(
        productCategory: ProductCategory, leafCategories: MutableSet<Long>
    ) {

        val children = productCategory.children

        if (children.isEmpty()) {
            logger.debug { "[${productCategory.name}] is the last category." }
            productCategory.id?.let { leafCategories.add(it) }

        } else {
            children.forEach { saveChildCategories(it, leafCategories) }
        }
    }

    private fun saveParentCategoriesName(
        productCategory: ProductCategory, parentCategories: MutableSet<String>
    ) {

        // 최상위 카테고리만 Map 저장
        parentCategories.add(productCategory.name)

        productCategory.parent?.let { saveParentCategoriesName(it, parentCategories) }
    }

    private fun splitAndSaveByFullCategory(
        fullCategory: String
    ): List<ProductCategory> {

        return buildList {
            var parent: ProductCategory? = null

            fullCategory.split("/").mapNotNull { it.trim().takeIf { it.isNotEmpty() } }.forEach { categoryName ->
                val category = productCategoryRepository.findByNameAndParent(categoryName, parent)
                    ?: productCategoryRepository.save(
                        ProductCategory(
                            name = categoryName, parent = parent
                        )
                    ).also { logger.info { "[$categoryName] category is not exist, so we create new category." } }

                add(category)
                parent = category
            }
        }
    }

    private fun findAndSaveBySaleState(
        requestSaleState: String
    ): ProductSaleState? {

        val saleState = SaleState.fromString(requestSaleState.uppercase())

        return productSaleStateRepository.findByName(saleState) ?: productSaleStateRepository.save(
            ProductSaleState(
                name = saleState
            )
        ).also { logger.info { "[$saleState] sale state is not exist, so we create new sale state." } }
    }
}