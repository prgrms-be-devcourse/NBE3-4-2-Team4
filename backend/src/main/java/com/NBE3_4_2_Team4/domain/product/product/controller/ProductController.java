package com.NBE3_4_2_Team4.domain.product.product.controller;

import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.*;
import static com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "포인트 상품 관리", description = "상품 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회합니다.")
    RsData<List<GetItem>> getAllProducts() {

        List<GetItem> products = productService.getProducts();

        return new RsData<>(
                "200-1",
                "%d건의 상품이 조회되었습니다.".formatted(products.size()),
                products
        );
    }

    @GetMapping
    @Operation(summary = "전체 상품 조회 with 검색", description = "전체 상품을 키워드, 페이징 처리하여 조회합니다.")
    RsData<PageDto<GetItem>> getAllProductsByKeywordWithPaging(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "12") int pageSize,
            @RequestParam(name = "search_keyword_type", defaultValue = "ALL") ProductSearchKeywordType searchKeywordType,
            @RequestParam(name = "search_keyword", defaultValue = "") String searchKeyword,
            @RequestParam(name = "category_keyword", defaultValue = "") String categoryKeyword,
            @RequestParam(name = "sale_state_keyword", defaultValue = "ALL") String saleStateKeyword
            ) {

        PageDto<GetItem> products = productService.getProducts(
                page,
                pageSize,
                searchKeywordType,
                searchKeyword,
                categoryKeyword,
                saleStateKeyword
        );

        return new RsData<>(
                "200-1",
                "%d건의 상품이 조회되었습니다.".formatted(products.getItems().size()),
                products
        );
    }

    @GetMapping("/categories/all")
    @Operation(summary = "카테고리별 상품 조회", description = "카테고리별 상품을 조회합니다.")
    RsData<GetItemsByKeyword> getProductsByCategory(
            @RequestParam(name = "category_keyword") String categoryKeyword
    ) {

        GetItemsByKeyword products = productService.getProductsByCategoryKeyword(categoryKeyword);

        return new RsData<>(
                "200-1",
                "%d건의 상품이 조회되었습니다.".formatted(products.products().size()),
                products
        );
    }

    @GetMapping("/categories")
    @Operation(summary = "카테고리별 상품 조회 (페이징)", description = "카테고리별 상품을 페이징 처리하여 조회합니다.")
    RsData<PageDto<GetItem>> getProductsByCategoryWithPaging(
            @RequestParam(name = "category_keyword") String categoryKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "12") int pageSize
    ) {

        PageDtoWithKeyword<GetItem> products = productService.getProductsByCategoryKeyword(categoryKeyword, page, pageSize);

        return new RsData<>(
                "200-1",
                "%d건의 상품이 조회되었습니다.".formatted(products.getItems().size()),
                products
        );
    }

    @GetMapping("/categories/keyword")
    @Operation(summary = "상품 카테고리 키워드 조회", description = "전체 상품 카테고리의 키워드를 조회합니다.")
    RsData<List<String>> getCategories(
    ) {

        List<String> categoriesName = productService.findCategoriesName();

        return new RsData<>(
                "200-1",
                "%d건의 상품 카테고리가 조회되었습니다.".formatted(categoriesName.size()),
                categoriesName
        );
    }

    @GetMapping("/states/all")
    @Operation(summary = "판매 상태별 상품 조회", description = "판매 상태별 상품을 조회합니다.")
    RsData<GetItemsByKeyword> getProductsBySaleState(
            @RequestParam(name = "sale_state_keyword") String saleStateKeyword
    ) {

        GetItemsByKeyword products = productService.getProductsBySaleStateKeyword(saleStateKeyword);

        return new RsData<>(
                "200-1",
                "%d건의 상품이 조회되었습니다.".formatted(products.products().size()),
                products
        );
    }

    @GetMapping("/states")
    @Operation(summary = "판매 상태별 상품 조회 (페이징)", description = "판매 상태별 상품을 페이징 처리하여 조회합니다.")
    RsData<PageDto<GetItem>> getProductsBySaleStateWithPaging(
            @RequestParam(name = "sale_state_keyword") String saleStateKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "12") int pageSize
    ) {

        PageDtoWithKeyword<GetItem> products = productService.getProductsBySaleStateKeyword(
                saleStateKeyword, page, pageSize);

        return new RsData<>(
                "200-1",
                "%d건의 상품이 조회되었습니다.".formatted(products.getItems().size()),
                products
        );
    }

    @GetMapping("/states/keyword")
    @Operation(summary = "판매 상태 키워드 조회", description = "판매 상태 키워드를 조회합니다.")
    RsData<List<String>> getSaleStates(
    ) {

        List<String> saleStateNames = productService.findSaleStateNames();

        return new RsData<>(
                "200-1",
                "%d건의 상품 판매 상태 키워드가 조회되었습니다.".formatted(saleStateNames.size()),
                saleStateNames
        );
    }

    @GetMapping("/{product_id}")
    @Operation(summary = "단건 상품 조회", description = "단건 상품을 조회합니다.")
    RsData<GetItem> getProduct(
            @PathVariable(name = "product_id") Long productId
    ) {

        GetItem product = productService.getProduct(productId);

        return new RsData<>(
                "200-1",
                "%d번 상품이 조회되었습니다.".formatted(product.getProductId()),
                product
        );
    }

    @PostMapping
    @Operation(summary = "단건 상품 생성", description = "단건 상품을 생성합니다.")
    RsData<GetItem> writeProduct(
            @RequestBody writeItem request
    ) {

        GetItem product = productService.writeProduct(request);

        return new RsData<>(
                "200-1",
                "%d번 상품이 생성되었습니다.".formatted(product.getProductId()),
                product
        );
    }

    @PatchMapping("/{product_id}")
    @Operation(summary = "단건 상품 수정", description = "단건 상품을 수정합니다.")
    RsData<GetItem> updateProduct(
            @PathVariable(name = "product_id") Long productId,
            @RequestBody updateItem request
    ) {

        GetItem product = productService.updateProduct(productId, request);

        return new RsData<>(
                "200-1",
                "%d번 상품이 수정되었습니다.".formatted(product.getProductId()),
                product
        );
    }

    @DeleteMapping("/{product_id}")
    @Operation(summary = "단건 상품 삭제", description = "단건 상품을 삭제합니다.")
    RsData<GetItem> deleteProduct(
            @PathVariable(name = "product_id") Long productId
    ) {

        productService.deleteProduct(productId);

        return new RsData<>(
                "200-1",
                "%d번 상품이 삭제되었습니다.".formatted(productId)
        );
    }
}
