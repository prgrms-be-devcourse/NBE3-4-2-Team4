package com.NBE3_4_2_Team4.domain.product.product.service;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.category.repository.ProductCategoryRepository;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import com.NBE3_4_2_Team4.domain.product.saleState.repository.ProductSaleStateRepository;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import com.NBE3_4_2_Team4.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.*;
import static com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSaleStateRepository productSaleStateRepository;

    @Transactional(readOnly = true)
    public long countProducts() {
        return productRepository.count();
    }


    @Transactional(readOnly = true)
    public List<GetItem> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new GetItem(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()))
                .toList();

    }

    @Transactional(readOnly = true)
    public PageDto<GetItem> getProducts(int page, int pageSize) {

        Pageable pageable = Ut.pageable.makePageable(page, pageSize);

        Page<Product> products = productRepository.findAll(pageable);

        return new PageDto<>(products.map(product -> new GetItem(
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
                        .map(product -> new GetItem(
                                product,
                                makeFullCategory(product),
                                product.getSaleState().getName()
                        ))
                        .toList());
    }

    @Transactional(readOnly = true)
    public PageDtoWithKeyword<GetItem> getProductsByCategoryKeyword(String categoryKeyword, int page, int pageSize) {

        // keyword 포함되는 categories 가져오기
        List<ProductCategory> categories = productCategoryRepository.findByNameContainingOrderByIdAsc(categoryKeyword);

        // categories 내 최하위 카테고리를 모두 찾아서 TreeMap에 저장
        Set<Long> leafCategories = new TreeSet<>();
        for (ProductCategory productCategory : categories) {
            saveChildCategories(productCategory, leafCategories);
        }

        Pageable pageable = Ut.pageable.makePageable(page, pageSize);

        // 최하위 카테고리에 해당하는 상품들 페이징 처리하여 조회
        Page<Product> products = productRepository.findByCategoryIdIn(leafCategories, pageable);

        return new PageDtoWithKeyword<>(
                products.map(product -> new GetItem(
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
                        .map(product -> new GetItem(
                                product,
                                makeFullCategory(product),
                                product.getSaleState().getName()
                        ))
                        .toList());
    }

    @Transactional(readOnly = true)
    public PageDtoWithKeyword<GetItem> getProductsBySaleStateKeyword(String saleStateKeyword, int page, int pageSize) {

        // 판매 상태 파라미터 유효성 체크
        SaleState saleState = SaleState.fromString(saleStateKeyword.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid saleState keyword: " + saleStateKeyword));

        Pageable pageable = Ut.pageable.makePageable(page, pageSize);

        Page<Product> products = productRepository.findBySaleStateLike(saleState, pageable);

        return new PageDtoWithKeyword<>(
                products.map(product -> new GetItem(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()
                )),
                saleStateKeyword
        );
    }

    @Transactional(readOnly = true)
    public GetItem getProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + productId));

        return new GetItem(
                product,
                makeFullCategory(product),
                product.getSaleState().getName()
        );
    }

    @Transactional
    public GetItem writeProduct(writeItem request) {

        List<ProductCategory> categories = splitAndSaveByFullCategory(request.getProductCategory());

        ProductSaleState saleState = findAndSaveBySaleState(request.getProductSaleState())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product saleState: " + request.getProductSaleState()));

        Product product = productRepository.save(request.toProduct(categories.getLast(), saleState));

        return new GetItem(
                product,
                makeFullCategory(product),
                product.getSaleState().getName()
        );
    }

    @Transactional
    public GetItem updateProduct(Long productId, updateItem request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(productId + "번 상품을 찾을 수 없습니다."));

        if (request.getProductName() != null) {
            product.updateName(request.getProductName());
        }

        if (request.getProductPrice() != null) {
            product.updatePrice(request.getProductPrice());
        }

        if (request.getProductDescription() != null) {
            product.updateDescription(request.getProductDescription());
        }

        if (request.getProductImageUrl() != null) {
            product.updateImageUrl(request.getProductImageUrl());
        }

        if (request.getProductCategory() != null) {
            List<ProductCategory> categories = splitAndSaveByFullCategory(request.getProductCategory());
            product.updateCategory(categories.getLast());
        }

        if (request.getProductSaleState() != null) {
            ProductSaleState saleState = findAndSaveBySaleState(request.getProductSaleState())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product saleState: " + request.getProductSaleState()));

            product.updateSaleState(saleState);
        }

        Product updateProduct = productRepository.save(product);

        return new GetItem(
                updateProduct,
                makeFullCategory(updateProduct),
                product.getSaleState().getName()
        );
    }

    @Transactional
    public void deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(productId + "번 상품을 찾을 수 없습니다."));

        productRepository.delete(product);
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


    private List<ProductCategory> splitAndSaveByFullCategory(String fullCategory) {

        List<ProductCategory> categories = new ArrayList<>();
        AtomicReference<ProductCategory> parent = new AtomicReference<>(null);

        StringTokenizer st = new StringTokenizer(fullCategory, "/");
        while (st.hasMoreTokens()) {

            String categoryName = st.nextToken().trim();
            if (categoryName.isEmpty()) {
                continue;
            }

            // category가 존재하지 않을 경우, 새로 생성 후 저장
            ProductCategory category = productCategoryRepository.findByNameAndParent(categoryName, parent.get())
                    .orElseGet(() -> {
                        ProductCategory saved = ProductCategory.builder()
                                .name(categoryName)
                                .parent(parent.get())
                                .build();
                        return productCategoryRepository.save(saved);
                    });

            categories.add(category);
            parent.set(category);
        }

        return categories;
    }

    private Optional<ProductSaleState> findAndSaveBySaleState(String requestSaleState) {

        // 판매 상태 파라미터 유효성 체크
        SaleState saleState = SaleState.fromString(requestSaleState.toUpperCase())
                .orElseGet(() -> null);

        if (saleState == null) {
            return Optional.empty();
        }

        // 판매 상태가 존재하지 않을 경우,
        ProductSaleState productSaleState = productSaleStateRepository.findByName(saleState)
                .orElseGet(() -> {
                    ProductSaleState saved = ProductSaleState.builder()
                            .name(saleState)
                            .build();
                    return productSaleStateRepository.save(saved);
                });

        return Optional.of(productSaleState);
    }
}
