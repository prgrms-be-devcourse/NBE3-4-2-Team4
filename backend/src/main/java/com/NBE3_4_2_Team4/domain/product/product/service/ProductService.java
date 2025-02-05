package com.NBE3_4_2_Team4.domain.product.product.service;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.category.repository.ProductCategoryRepository;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional(readOnly = true)
    public List<GetItems> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new GetItems(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()))
                .toList();

    }

    @Transactional(readOnly = true)
    public PageDto<GetItems> getProducts(int page, int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.findAll(pageable);

        return new PageDto<>(products.map(product -> new GetItems(
                product,
                makeFullCategory(product),
                product.getSaleState().getName()
        )));
    }

    @Transactional(readOnly = true)
    public GetItemsByKeyword getProductsByCategoryKeyword(String categoryKeyword) {

        // keyword 포함되는 categories 가져오기
        List<ProductCategory> categories = productCategoryRepository.findByNameContainingOrderByIdAsc(categoryKeyword);
        if (categories.isEmpty()) {
            return new GetItemsByKeyword(categoryKeyword, Collections.emptyList());
        }

        // categories 내 최하위 카테고리를 모두 찾아서 TreeMap에 저장
        Set<Long> leafCategories = new TreeSet<>();
        for (ProductCategory productCategory : categories) {
            saveChildCategories(productCategory, leafCategories);
        }

        // 최하위 카테고리에 해당하는 상품 조회
        List<Product> products = productRepository.findByCategoryIdIn(leafCategories);

        return new GetItemsByKeyword(categoryKeyword,
                products.stream()
                        .map(product -> new GetItems(
                                product,
                                makeFullCategory(product),
                                product.getSaleState().getName()
                        ))
                        .toList());
    }

    @Transactional(readOnly = true)
    public PageDtoWithKeyword<GetItems> getProductsByCategoryKeyword(String categoryKeyword, int page, int pageSize) {

        // keyword 포함되는 categories 가져오기
        List<ProductCategory> categories = productCategoryRepository.findByNameContainingOrderByIdAsc(categoryKeyword);

        // categories 내 최하위 카테고리를 모두 찾아서 TreeMap에 저장
        Set<Long> leafCategories = new TreeSet<>();
        for (ProductCategory productCategory : categories) {
            saveChildCategories(productCategory, leafCategories);
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        // 최하위 카테고리에 해당하는 상품들 페이징 처리하여 조회
        Page<Product> products = productRepository.findByCategoryIdIn(leafCategories, pageable);

        return new PageDtoWithKeyword<>(
                products.map(product -> new GetItems(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()
                )),
                categoryKeyword
        );
    }

    @Transactional(readOnly = true)
    public GetItemsByKeyword getProductsBySaleStateKeyword(String saleStateKeyword) {

        // 판매 상태 파라미터 유효성 체크
        SaleState saleState = SaleState.fromString(saleStateKeyword.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid saleState keyword: " + saleStateKeyword));

        List<Product> products = productRepository.findBySaleStateLike(saleState);

        return new GetItemsByKeyword(saleStateKeyword,
                products.stream()
                        .map(product -> new GetItems(
                                product,
                                makeFullCategory(product),
                                product.getSaleState().getName()
                        ))
                        .toList());
    }

    @Transactional(readOnly = true)
    public PageDtoWithKeyword<GetItems> getProductsBySaleStateKeyword(String saleStateKeyword, int page, int pageSize) {

        // 판매 상태 파라미터 유효성 체크
        SaleState saleState = SaleState.fromString(saleStateKeyword.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid saleState keyword: " + saleStateKeyword));

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.findBySaleStateLike(saleState, pageable);

        return new PageDtoWithKeyword<>(
                products.map(product -> new GetItems(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()
                )),
                saleStateKeyword
        );
    }

    private String makeFullCategory(Product product) {

        StringBuilder sb = new StringBuilder();

        ProductCategory category = product.getCategory();

        // 상위 카테고리가 없을 때까지 카테고리 추가
        while (category != null) {
            sb.insert(0, category.getName() + "/");
            category = category.getParent();
        }

        // 마지막 '/' 문자 제거
        if (!sb.isEmpty() && sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    private void saveChildCategories(ProductCategory productCategory, Set<Long> leafCategories) {

        List<ProductCategory> children = productCategory.getChildren();

        // 최하위 카테고리만 Map 저장
        if (children.isEmpty()) {
            leafCategories.add(productCategory.getId());
            return;
        }

        children.forEach(child -> {
            saveChildCategories(child, leafCategories);
        });
    }
}
