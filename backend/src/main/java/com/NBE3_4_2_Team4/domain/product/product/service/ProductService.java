package com.NBE3_4_2_Team4.domain.product.product.service;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.category.repository.ProductCategoryRepository;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.findAll(pageRequest);

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
        Map<Long, ProductCategory> leafCategories = new TreeMap<>();
        for (ProductCategory productCategory : categories) {
            saveChildCategories(productCategory, leafCategories);
        }

        // 최하위 카테고리에 해당하는 상품 찾아서 List에 저장
        List<GetItems> items = new ArrayList<>();

        for (ProductCategory category : leafCategories.values()) {
            for (Product product : category.getProducts()) {
                items.add(new GetItems(
                        product,
                        makeFullCategory(product),
                        product.getSaleState().getName()
                ));
            }
        }

        return new GetItemsByKeyword(categoryKeyword, items);
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

    private void saveChildCategories(ProductCategory productCategory, Map<Long, ProductCategory> leafCategories) {

        List<ProductCategory> children = productCategory.getChildren();

        // 최하위 카테고리만 Map 저장
        if (children.isEmpty()) {
            leafCategories.put(productCategory.getId(), productCategory);
            return;
        }

        children.forEach(child -> {
            saveChildCategories(child, leafCategories);
        });
    }
}
