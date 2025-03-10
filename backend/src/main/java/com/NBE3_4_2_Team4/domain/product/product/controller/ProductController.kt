package com.NBE3_4_2_Team4.domain.product.product.controller

import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.UpdateItem
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.WriteItem
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.GetItem
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.GetItemsByKeyword
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.dto.PageDto
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "포인트 상품 관리", description = "상품 API")
class ProductController(

    private val productService: ProductService
) {

    @GetMapping("/all")
    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회합니다.")
    fun getAllProducts(): RsData<List<GetItem>> {

        val products = productService.getProducts()

        return RsData(
            "200-1",
            "${products.size}건의 상품이 조회되었습니다.",
            products
        )
    }

    @GetMapping
    @Operation(summary = "전체 상품 조회 with 검색", description = "전체 상품을 키워드, 페이징 처리하여 조회합니다.")
    fun getAllProductsByKeywordWithPaging(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(name = "page_size", defaultValue = "12") pageSize: Int,
        @RequestParam(name = "search_keyword_type", defaultValue = "ALL") searchKeywordType: ProductSearchKeywordType,
        @RequestParam(name = "search_keyword", defaultValue = "") searchKeyword: String,
        @RequestParam(name = "category_keyword", defaultValue = "") categoryKeyword: String,
        @RequestParam(name = "sale_state_keyword", defaultValue = "ALL") saleStateKeyword: String
    ): RsData<PageDto<GetItem>> {

        val products = productService.getProducts(
            page,
            pageSize,
            searchKeywordType,
            searchKeyword,
            categoryKeyword,
            saleStateKeyword
        )

        return RsData(
            "200-1",
            "${products.items.size}건의 상품이 조회되었습니다.",
            products
        )
    }

    @GetMapping("/categories/all")
    @Operation(summary = "카테고리별 상품 조회", description = "카테고리별 상품을 조회합니다.")
    fun getProductsByCategory(
        @RequestParam(name = "category_keyword") categoryKeyword: String
    ): RsData<GetItemsByKeyword> {

        val products = productService.getProductsByCategoryKeyword(categoryKeyword)

        return RsData(
            "200-1",
            "${products.products.size}건의 상품이 조회되었습니다.",
            products
        )
    }

    @GetMapping("/categories")
    @Operation(summary = "카테고리별 상품 조회 (페이징)", description = "카테고리별 상품을 페이징 처리하여 조회합니다.")
    fun getProductsByCategoryWithPaging(
        @RequestParam(name = "category_keyword") categoryKeyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(name = "page_size", defaultValue = "12") pageSize: Int
    ): RsData<PageDto<GetItem>> {

        val products = productService.getProductsByCategoryKeyword(categoryKeyword, page, pageSize)

        return RsData(
            "200-1",
            "${products.items.size}건의 상품이 조회되었습니다.",
            products
        )
    }

    @GetMapping("/categories/keyword")
    @Operation(summary = "상품 카테고리 키워드 조회", description = "전체 상품 카테고리의 키워드를 조회합니다.")
    fun getCategories(
    ): RsData<List<String>> {

        val categoriesName = productService.findCategoriesName()

        return RsData(
            "200-1",
            "${categoriesName.size}건의 상품 카테고리가 조회되었습니다.",
            categoriesName
        )
    }

    @GetMapping("/states/all")
    @Operation(summary = "판매 상태별 상품 조회", description = "판매 상태별 상품을 조회합니다.")
    fun getProductsBySaleState(
        @RequestParam(name = "sale_state_keyword") saleStateKeyword: String
    ): RsData<GetItemsByKeyword> {

        val products = productService.getProductsBySaleStateKeyword(saleStateKeyword)

        return RsData(
            "200-1",
            "${products.products.size}건의 상품이 조회되었습니다.",
            products
        )
    }

    @GetMapping("/states")
    @Operation(summary = "판매 상태별 상품 조회 (페이징)", description = "판매 상태별 상품을 페이징 처리하여 조회합니다.")
    fun getProductsBySaleStateWithPaging(
        @RequestParam(name = "sale_state_keyword") saleStateKeyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(name = "page_size", defaultValue = "12") pageSize: Int
    ): RsData<PageDto<GetItem>> {

        val products = productService.getProductsBySaleStateKeyword(saleStateKeyword, page, pageSize)

        return RsData(
            "200-1",
            "${products.items.size}건의 상품이 조회되었습니다.",
            products
        )
    }

    @GetMapping("/states/keyword")
    @Operation(summary = "판매 상태 키워드 조회", description = "판매 상태 키워드를 조회합니다.")
    fun getSaleStates(
    ): RsData<List<String>> {

        val saleStateNames = productService.findSaleStateNames()

        return RsData(
            "200-1",
            "${saleStateNames.size}건의 상품 판매 상태 키워드가 조회되었습니다.",
            saleStateNames
        )
    }

    @GetMapping("/{product_id}")
    @Operation(summary = "단건 상품 조회", description = "단건 상품을 조회합니다.")
    fun getProduct(
        @PathVariable(name = "product_id") productId: Long
    ): RsData<GetItem> {

        val product = productService.getProduct(productId)

        return RsData(
            "200-1",
            "${product.productId}번 상품이 조회되었습니다.",
            product
        )
    }

    @PostMapping
    @Operation(summary = "단건 상품 생성", description = "단건 상품을 생성합니다.")
    fun writeProduct(
        @RequestBody request: WriteItem
    ): RsData<GetItem> {

        val product = productService.writeProduct(request)

        return RsData(
            "200-1",
            "${product.productId}번 상품이 생성되었습니다.",
            product
        )
    }

    @PatchMapping("/{product_id}")
    @Operation(summary = "단건 상품 수정", description = "단건 상품을 수정합니다.")
    fun updateProduct(
        @PathVariable(name = "product_id") productId: Long,
        @RequestBody request: UpdateItem
    ): RsData<GetItem> {

        val product = productService.updateProduct(productId, request)

        return RsData(
            "200-1",
            "${product.productId}번 상품이 수정되었습니다.",
            product
        )
    }

    @DeleteMapping("/{product_id}")
    @Operation(summary = "단건 상품 삭제", description = "단건 상품을 삭제합니다.")
    fun deleteProduct(
        @PathVariable(name = "product_id") productId: Long
    ): RsData<Unit> {

        productService.deleteProduct(productId)

        return RsData(
            "200-1",
            "${productId}번 상품이 삭제되었습니다."
        )
    }


}