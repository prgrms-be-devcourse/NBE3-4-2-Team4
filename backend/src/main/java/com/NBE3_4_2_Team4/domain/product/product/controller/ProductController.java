package com.NBE3_4_2_Team4.domain.product.product.controller;

import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "ProductController", description = "상품 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회합니다.")
    RsData<List<GetItems>> getAllProducts() {

        List<GetItems> products = productService.getProducts();

        return new RsData<>(
                "200-1",
                "OK",
                products
        );
    }

    @GetMapping
    @Operation(summary = "전체 상품 조회 (페이징)", description = "전체 상품을 페이징 처리하여 조회합니다.")
    RsData<PageDto<GetItems>> getAllProductsWithPaging(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int pageSize) {

        PageDto<GetItems> products = productService.getProducts(page, pageSize);

        return new RsData<>(
                "200-1",
                "OK",
                products
        );
    }

    @GetMapping("/category/all")
    @Operation(summary = "카테고리별 상품 조회", description = "카테고리별 상품을 조회합니다.")
    RsData<GetItemsByKeyword> getProductsByCategory(
            @RequestParam(name = "category_keyword") String categoryKeyword) {

        GetItemsByKeyword products = productService.getProductsByCategoryKeyword(categoryKeyword);

        return new RsData<>(
                "200-1",
                "OK",
                products
        );
    }

    @GetMapping("/category")
    @Operation(summary = "카테고리별 상품 조회 (페이징)", description = "카테고리별 상품을 페이징 처리하여 조회합니다.")
    RsData<PageDto<GetItems>> getProductsByCategoryWithPaging(
            @RequestParam(name = "category_keyword") String categoryKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int pageSize
    ) {

        PageDtoWithKeyword<GetItems> products = productService.getProductsByCategoryKeyword(categoryKeyword, page, pageSize);

        return new RsData<>(
                "200-1",
                "OK",
                products
        );
    }

    @GetMapping("/state/all")
    @Operation(summary = "판매 상태별 상품 조회", description = "판매 상품별 상품을 조회합니다.")
    RsData<GetItemsByKeyword> getProductsBySaleState(
            @RequestParam(name = "sale_state_keyword") String saleStateKeyword) {

        GetItemsByKeyword products = productService.getProductsBySaleStateKeyword(saleStateKeyword);

        return new RsData<>(
                "200-1",
                "OK",
                products
        );
    }
}
